package com.oushu.phoenix.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.concurrent.*;

@Slf4j
public class PhoenixDataSource {

    public static String krbEnableKey = "hbase.krb.enabled";
    public static String HadoopUserName = "hbase.hadoop.user.name";
    public static String krbConfKey = "hbase.krb.conf";
    public static String krbUserKey = "hbase.krb.login.principal";
    public static String krbKeyTabPathKey = "hbase.krb.login.keytab";

    private static DruidDataSource pds = null;

    public static DruidDataSource createPhoenixDataSource() {
        if ( pds != null || true ) {
            return pds;
        }
        log.info("初始化phoenix连接池...");
        long startTime = System.currentTimeMillis();

        //读取自定义配置
        Configuration conf = new Configuration();
        conf.setClassLoader(PhoenixDataSource.class.getClassLoader());
        conf.addResource("phoenix-custom-site-default.xml");
        conf.addResource("phoenix-custom-site.xml");

        Configuration kerConf = new Configuration();
        kerConf.setClassLoader(PhoenixDataSource.class.getClassLoader());
        kerConf.addResource("hbase-site.xml");
        System.setProperty("HADOOP_USER_NAME", conf.get(HadoopUserName));
        String user = conf.get(krbUserKey);
        boolean krbEnabled = conf.getBoolean(krbEnableKey, false);
        if (krbEnabled){
            System.setProperty("sun.security.krb5.debug", "true");  //开启详细debug参数
            kerConf.set("hadoop.security.authentication", "Kerberos");
            System.setProperty("java.security.krb5.conf", conf.get(krbConfKey));
            UserGroupInformation.setConfiguration(kerConf);
        }

        try {
            String zookeeperQuorum = kerConf.get("hbase.zookeeper.quorum");
            String zookeeperPort = kerConf.get("hbase.zookeeper.property.clientPort");
            String znode = kerConf.get("zookeeper.znode.parent");
            String url = "jdbc:phoenix:" + zookeeperQuorum + ":" + zookeeperPort + ":" + znode;
            if (krbEnabled){
                UserGroupInformation.loginUserFromKeytab(user, conf.get(krbKeyTabPathKey));
                url += ":" + user + ":" + conf.get(krbKeyTabPathKey);
            }
            log.warn("phoenix连接URL: " + url);
            // conn = DriverManager.getConnection(url).unwrap(PhoenixConnection.class);
            int maxWait = Integer.parseInt(conf.get("druid.datasource.max.wait"));
            pds = new DruidDataSource();
            pds.setDriverClassName("org.apache.phoenix.jdbc.PhoenixDriver");
            pds.setUrl(url);
            pds.setUsername("");
            pds.setPassword("");
            pds.setInitialSize(Integer.parseInt(conf.get("druid.datasource.init.size")));
            pds.setMaxActive(Integer.parseInt(conf.get("druid.datasource.max.active")));
            pds.setMinIdle(Integer.parseInt(conf.get("druid.datasource.min.idle")));
            pds.setMaxWait(maxWait);
            pds.setQueryTimeout(Integer.parseInt(conf.get("druid.datasource.query.timeout")));
            pds.setPoolPreparedStatements(Boolean.parseBoolean(conf.get("druid.datasource.pool.stmt")));
            pds.setMaxOpenPreparedStatements(Integer.parseInt(conf.get("druid.datasource.max.open.stmt")));
            pds.setValidationQuery(conf.get("druid.datasource.validate.query"));
            pds.setTestOnBorrow(Boolean.parseBoolean(conf.get("druid.datasource.test.on.borrow")));
            pds.setTestOnReturn(Boolean.parseBoolean(conf.get("druid.datasource.test.on.return")));
            pds.setTestWhileIdle(Boolean.parseBoolean(conf.get("druid.datasource.test.while.idle")));
            pds.setTimeBetweenEvictionRunsMillis(60000);
            pds.setMinEvictableIdleTimeMillis(25200000);
            pds.setRemoveAbandoned(Boolean.parseBoolean(conf.get("druid.datasource.remove.abandoned")));
            pds.setRemoveAbandonedTimeout(Integer.parseInt(conf.get("druid.datasource.remove.abandoned.timeout")));
            pds.setLogAbandoned(Boolean.parseBoolean(conf.get("druid.datasource.remove.abandoned.log")));
            pds.setFilters(conf.get("druid.datasource.filter"));
            // 设置剔除异常连接机制
            pds.setExceptionSorter(new PhoenixExceptionSorter());
            try {
                ExecutorService exec = Executors.newFixedThreadPool(1);
                Callable<Connection> call = new Callable<Connection>() {
                    public Connection call() throws Exception {
                        pds.init();
                        return null;
                    }
                };
                Future<Connection> future = exec.submit(call);
                future.get(maxWait, TimeUnit.MILLISECONDS);
                exec.shutdown();
            } catch (InterruptedException e) {
                log.error("获取连接线程中断！url=" + url, e);
                throw new Exception(e);
            } catch (ExecutionException e) {
                log.error("获取连接出错！url=" + url, e);
                throw new Exception(e);
            } catch (TimeoutException e) {
                log.error("获取连接超时！url=" + url, e);
                throw new Exception(e);
            }
//            pds.init();

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        log.info("初始化phoenix连接池完成，耗时：" + (System.currentTimeMillis() - startTime) + "ms.");
        return pds;
    }

    public static DruidDataSource getPhoenixDataSource() {
        return pds;
    }

}

package com.oushu.phoenix.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.concurrent.*;

public class PhoenixDataSource {

    private static Logger log = Logger.getLogger(PhoenixDataSource.class);
    private static DruidDataSource pds = null;

    public static DruidDataSource createPhoenixDataSource() {
        if ( pds != null ) {
            return pds;
        }
        log.info("初始化phoenix连接池...");
        long startTime = System.currentTimeMillis();

        // 连接hadoop环境，进行 Kerberos认证
//        Configuration conf = new Configuration();
        // conf.set("hadoop.security.authentication", "Kerberos");

        // linux 环境会默认读取/etc/nokrb.cnf文件，win不指定会默认读取C:/Windows/krb5.ini
        /*if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            System.setProperty("java.security.krb5.conf", "/conf/krb5.conf");
        }*/
        //System.setProperty("java.security.krb5.conf", "conf/krb5.conf");
        //        UserGroupInformation.setConfiguration(conf);

        //读取自定义配置
        Configuration conf = new Configuration();
        conf.setClassLoader(PhoenixDataSource.class.getClassLoader());
        conf.addResource("phoenix-custom-site-default.xml");
        conf.addResource("phoenix-custom-site.xml");
        String s = conf.get("hbase.phoenix.max.num");
//        CTApi.MaxFirstCount = Integer.parseInt(s);
        try {
            //UserGroupInformation.loginUserFromKeytab("hbase/_HOST@HADOOP.COM", "conf/hbasehost.keytab");
//            Class.forName("");
            // kerberos环境下Phoenix的jdbc字符串为 jdbc:phoenix:zk:2181:/znode:principal:keytab
            Configuration entries = HBaseConfiguration.create();
            String zookeeperQuorum = entries.get("hbase.zookeeper.quorum");
            String zookeeperPort = entries.get("hbase.zookeeper.property.clientPort");
            String znode = entries.get("zookeeper.znode.parent");

            String url = "jdbc:phoenix:" + zookeeperQuorum + ":" + zookeeperPort + ":" + znode;
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

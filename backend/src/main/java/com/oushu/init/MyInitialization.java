package com.oushu.init;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

/**
 * 该类可用于SpringBoot应用启动后提前做一些初始化操作，比如缓存数据
 */
@Slf4j
@Component
public class MyInitialization implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("应用启动了，我准备初始化数据了");
    }
}


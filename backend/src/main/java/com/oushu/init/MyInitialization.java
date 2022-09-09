package com.oushu.init;

//import com.oushu.phoenix.jdbc.PhoenixDataSource;
import com.oushu.phoenix.jdbc.PhoenixDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 该类可用于SpringBoot应用启动后提前做一些初始化操作，比如缓存数据
 */
@Slf4j
@Component
public class MyInitialization implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("应用启动了，我准备初始化数据了");
//        PhoenixDataSource.createPhoenixDataSource();
    }
}


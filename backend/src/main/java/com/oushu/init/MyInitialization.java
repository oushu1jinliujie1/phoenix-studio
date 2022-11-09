package com.oushu.init;

import com.oushu.config.SpringContextUtils;
import com.oushu.config.Studio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;


/**
 * 该类可用于SpringBoot应用启动后提前做一些初始化操作，比如缓存数据
 */
@Slf4j
@Component
public class MyInitialization implements ApplicationRunner {

    @Autowired
    private Studio studio;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("应用启动了，我准备初始化数据了");
    }
}


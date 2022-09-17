package com.oushu;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class MainApplication {
    public static void main(String[] args) {
        // 启动Spring应用
//        System.setProperty("org.apache.commons.logging.LogFactory","org.apache.commons.logging.impl.SLF4JLogFactory");
        SpringApplication.run(MainApplication.class,args);
    }
}

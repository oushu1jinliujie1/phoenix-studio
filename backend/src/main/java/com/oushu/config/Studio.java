package com.oushu.config;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("phoenix.studio")
@Getter
@Setter
@ToString
public class Studio {
    private String hbase;
    private String phoenix;
}

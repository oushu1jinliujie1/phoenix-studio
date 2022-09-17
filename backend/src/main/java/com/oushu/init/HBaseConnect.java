package com.oushu.init;


import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HBaseConnect {
    public static Connection connection = null;

    static {
        try {
            connection = ConnectionFactory.createConnection();
        } catch (IOException e){
            System.out.println("连接获取失败");
            e.printStackTrace();
        }
    }

    public static void closeConnection() throws IOException {
        if (connection != null) {
            connection.close();
        }
    }
}

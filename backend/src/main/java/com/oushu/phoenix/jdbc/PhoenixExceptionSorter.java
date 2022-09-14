package com.oushu.phoenix.jdbc;

import com.alibaba.druid.pool.ExceptionSorter;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.Logger;

import java.sql.SQLException;
import java.util.Properties;
@Slf4j
public class PhoenixExceptionSorter implements ExceptionSorter {

    @Override
    public boolean isExceptionFatal(SQLException e) {
        if (e.getMessage().contains("Connection is null or closed")) {
            log.error("Delete unavailable phoenix connection", e);
            return true;
        }
        return false;
    }

    @Override
    public void configFromProperties(Properties properties) {

    }
}

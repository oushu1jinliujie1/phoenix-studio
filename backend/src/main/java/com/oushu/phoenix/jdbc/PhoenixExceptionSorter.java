package com.oushu.phoenix.jdbc;

import com.alibaba.druid.pool.ExceptionSorter;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Properties;

public class PhoenixExceptionSorter implements ExceptionSorter {
    private static Logger LOG = Logger.getLogger(PhoenixExceptionSorter.class);

    @Override
    public boolean isExceptionFatal(SQLException e) {
        if (e.getMessage().contains("Connection is null or closed")) {
            LOG.error("Delete unavailable phoenix connection", e);
            return true;
        }
        return false;
    }

    @Override
    public void configFromProperties(Properties properties) {

    }
}

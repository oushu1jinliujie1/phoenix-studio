package com.oushu.util;

import java.sql.JDBCType;
import java.util.Map;

import static java.sql.JDBCType.*;

public class DataType {
    public static Map<JDBCType, String> D;

    static {
        D.put(BIT, "BIT");
        D.put(TINYINT, "TINYINT");
        D.put(SMALLINT, "SMALLINT");
        D.put(INTEGER, "INTEGER");
        D.put(BIGINT, "BIGINT");
    }
}

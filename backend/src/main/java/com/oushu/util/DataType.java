package com.oushu.util;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;



public class DataType {
    public static Map<Integer, String> D = new HashMap<>();

    static {
        D.put(Types.BIT, "BIT");
        D.put(Types.TINYINT, "TINYINT");
        D.put(Types.SMALLINT, "SMALLINT");
        D.put(Types.INTEGER, "INTEGER");
        D.put(Types.BIGINT, "BIGINT");
        D.put(Types.FLOAT, "FLOAT");
        D.put(Types.REAL, "REAL");
        D.put(Types.DOUBLE, "DOUBLE");
        D.put(Types.NUMERIC, "NUMERIC");
        D.put(Types.DECIMAL, "DECIMAL");
    }

    public static boolean isNumber(Integer dataType) {
        String value = D.getOrDefault(dataType, "");
        return !value.isEmpty();
    }
}

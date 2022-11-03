package com.oushu.service;

import com.google.gson.JsonObject;
import com.oushu.model.CreateTableRequest;

import java.util.List;
import java.util.Map;

public interface SqlService {
    int execSQL(String sql);

    void saveTableComment(CreateTableRequest param);

    boolean saveSchemaComment(String schemaName, String comment);

    void saveColumnComment(String schemaName, String tableName, String columnName, String comment);

    void delTableComment(String schemaName, String tableName);

    String getTableComment(String schemaName, String tableName);

    String getColumnComment(String schemaName, String tableName, String columnName);

    List<Map<String, Object>> getColumnComment(String schemaName, String tableName);

    void delColumnComment(String schemaName, String tableName);

}

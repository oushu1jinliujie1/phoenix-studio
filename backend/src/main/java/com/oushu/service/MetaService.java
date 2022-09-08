package com.oushu.service;

import com.google.gson.JsonObject;
import com.oushu.model.BasicTableParam;
import com.oushu.model.IdxParam;

import java.util.List;
import java.util.Map;

public interface MetaService {

    List<Map<String, Object>> getSchemaList();
    long getTableNum(String schemaName);
    boolean delSchema(String schemaName);
    boolean schemaExist(String schemaName);
    List<Map<String, Object>> getTableList(BasicTableParam param);
    long getTableNumWithSearch(String schemaName, String tableName);
    boolean tableExist(String schemaName, String tableName);
    boolean delTable(String schemaName, String tableName);
    List<Map<String, Object>> getIdxList(BasicTableParam param);
    String getIdxState(String schemaName, String tableName);
    List<JsonObject> getTableColumns(String schemaName, String tableName);
    List<JsonObject> getTableColumnsWithLimit(BasicTableParam param);
    boolean delIdx(IdxParam param);
    JsonObject getTableInfo(String schemaName, String tableName);

}

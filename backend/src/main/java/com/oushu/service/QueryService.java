package com.oushu.service;

import com.google.gson.JsonObject;
import com.oushu.model.BasicTableParam;
import com.oushu.model.OsMeta;
import com.oushu.model.QueryTableName;
import com.oushu.model.TableName;

import java.util.List;
import java.util.Map;

public interface QueryService {
    boolean CreateQuery(OsMeta meta);
    List<Map<String, Object>> getQueryTableList(QueryTableName param);
    boolean queryNameDup(String queryTableName);
    boolean delQueryName(String queryTableName);
    JsonObject queryTableInfo(String queryTableName);
    boolean editQuery(OsMeta meta);
    List<Map<String, Object>> getConnectedQueryTableList(TableName tableName);
}

package com.oushu.service;

import com.google.gson.JsonObject;
import com.oushu.model.*;

import java.util.List;
import java.util.Map;

public interface QueryService {

    boolean CheckValide(OsMeta meta);
    boolean CreateQuery(OsMeta meta);
    List<MetaInfo> getQueryTableList(QueryTableName param);

    long getQueryTableCount(QueryTableName param);
    boolean queryNameDup(String queryTableName);
    boolean delQueryName(String queryTableName);
    MetaInfo queryTableInfo(String queryTableName);
    boolean editQuery(OsMeta meta);
    List<Map<String, Object>> getConnectedQueryTableList(TableName tableName);

    List<Map<String, Object>> querySearchTableData(SearchTableDataRequest tableName);

    List<Map<String, Object>> querySearchTableDataWithPK(SearchTableDataRequest tableName);

    List<Map<String, Object>> queryBasicTableData(SearchTableDataRequest tableName);
}

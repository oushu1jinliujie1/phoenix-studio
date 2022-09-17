package com.oushu.service.impl;

import com.google.gson.JsonObject;
import com.oushu.model.OsMeta;
import com.oushu.model.QueryTableName;
import com.oushu.model.TableName;
import com.oushu.phoenix.jdbc.PhoenixQuery;
import com.oushu.service.QueryService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QueryServiceImpl implements QueryService {

    private String metaTableName = "ct.os_meta";

    private PhoenixQuery pq = new PhoenixQuery();
    /**
     * @param meta
     * @return
     */
    @Override
    public boolean CreateQuery(OsMeta meta) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, meta.getQueryName());
        param.put(2, meta.getChineseName());
        param.put(3, meta.getDescription());
        String tables = Arrays.stream(meta.getTableNames())
                .map(item -> item.getNameWithoutQuote())
                .collect(Collectors.joining(","));
        param.put(4, tables);
        String columns = Arrays.stream(meta.getColumns())
                .map(item -> item.getColumnName())
                .collect(Collectors.joining(","));
        param.put(5, columns);
        String upSertSql = "upsert into " + metaTableName + " values ( ?, ?, ?, ?, ? )";
        int execute = pq.execute(upSertSql, param);
        return execute > 0;
    }

    /**
     * @param param
     * @return
     */
    @Override
    public List<Map<String, Object>> getQueryTableList(QueryTableName param) {
        Map<Integer,Object> sqlParam = new HashMap<>();
        int paramNum = 1;
        String sql = "select * from " + metaTableName;
        if (param.getQueryName() != null && !param.getQueryName().equals("")){
            sql += " where queryName like ? ";
            sqlParam.put(paramNum++, "%" + param.getQueryName() + "%");
        }
        sql += " limit ? offset ?";
        sqlParam.put(paramNum++, param.getLimit());
        sqlParam.put(paramNum++, param.getOffset());
        return pq.getListMap(sql, sqlParam);
    }

    /**
     * @param queryTableName
     * @return
     */
    @Override
    public boolean queryNameDup(String queryTableName) {
        String sql = "select count(1) as count from " + this.metaTableName
                + " where queryName = ?";
        Map<Integer,Object> sqlParam = new HashMap<>();
        sqlParam.put(1, queryTableName);
        JsonObject res = pq.executeQuery(sql, sqlParam);
        long count = res.get("COUNT").getAsLong();
        return count > 0;
    }

    /**
     * @param queryTableName
     * @return
     */
    @Override
    public boolean delQueryName(String queryTableName) {
        String sql = "delete from " + this.metaTableName
                + " where queryName = ?";
        Map<Integer,Object> sqlParam = new HashMap<>();
        sqlParam.put(1, queryTableName);
        int execute = pq.execute(sql, sqlParam);
        return execute >= 0;
    }

    /**
     * @param queryTableName
     * @return
     */
    @Override
    public JsonObject queryTableInfo(String queryTableName) {
        String sql = "select * from " + this.metaTableName
                + " where queryName = ?";
        Map<Integer,Object> sqlParam = new HashMap<>();
        sqlParam.put(1, queryTableName);
        return pq.executeQuery(sql, sqlParam);
    }

    /**
     * @param meta
     * @return
     */
    @Override
    public boolean editQuery(OsMeta meta) {
        return false;
    }

    /**
     * @param tableName
     * @return
     */
    @Override
    public List<Map<String, Object>> getConnectedQueryTableList(TableName tableName) {
        String sql = "select * from " + this.metaTableName
                + " where TABLENAMES like ?";
        Map<Integer,Object> sqlParam = new HashMap<>();
        sqlParam.put(1, "%" + tableName.getNameWithoutQuote() + "%");
        return pq.getListMap(sql, sqlParam);
    }
}

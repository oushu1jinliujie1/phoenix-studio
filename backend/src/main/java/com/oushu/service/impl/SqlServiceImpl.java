package com.oushu.service.impl;

import com.google.gson.JsonObject;
import com.oushu.config.Studio;
import com.oushu.model.Column;
import com.oushu.model.CreateTableRequest;
import com.oushu.phoenix.jdbc.PhoenixQuery;
import com.oushu.service.MetaService;
import com.oushu.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SqlServiceImpl implements SqlService {

    @Autowired
    private PhoenixQuery pq;

    @Autowired
    private MetaService metaService;

    /**
     * @param sql
     * @return
     */
    @Override
    public int execSQL(String sql) {
        return pq.execute(sql, new HashMap<>());
    }

    /**
     * @param param
     * @return
     */
    @Override
    public void saveTableComment(CreateTableRequest param) {
        Map<Integer,Object> sqlParam = new HashMap<>();
        sqlParam.put(1, param.getSchemaName());
        sqlParam.put(2, param.getTableName());
        sqlParam.put(3, param.getComment());
        pq.execute("upsert into ct.os_table values (?, ?, ?)", sqlParam);
        for (Column column : param.getColumns()) {
            saveColumnComment(param.getSchemaName(), param.getTableName(),
                    column.getColumnName(), column.getComment());
        }
    }

    /**
     * @param schemaName
     * @param comment
     * @return
     */
    @Override
    public boolean saveSchemaComment(String schemaName, String comment) {
        return false;
    }

    /**
     * @param schemaName
     * @param tableName
     * @param columnName
     * @param comment
     */
    @Override
    public void saveColumnComment(String schemaName, String tableName, String columnName, String comment) {
        Map<Integer,Object> sqlParam = new HashMap<>();
        sqlParam.put(1, schemaName);
        sqlParam.put(2, tableName);
        sqlParam.put(3, columnName);
        sqlParam.put(4, comment);
        pq.execute("upsert into ct.os_column values (?, ?, ?, ?)", sqlParam);
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    public void delTableComment(String schemaName, String tableName) {
        Map<Integer,Object> sqlParam = new HashMap<>();
        sqlParam.put(1, schemaName);
        sqlParam.put(2, tableName);
        pq.execute("delete from ct.os_table where SCHEMANAME = ? and TABLENAME = ?", sqlParam);
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    public String getTableComment(String schemaName, String tableName) {
        String sql = "select comment from ct.os_table where schemaName = ? and tableName = ?";
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        param.put(2, tableName);
        JsonObject jsonObject = pq.executeQuery(sql, param);
        return jsonObject != null ? (jsonObject.get("COMMENT").isJsonNull() ? "" : jsonObject.get("COMMENT").getAsString()) : "";
    }

    /**
     * @param schemaName
     * @param tableName
     * @param columnName
     * @return
     */
    @Override
    public String getColumnComment(String schemaName, String tableName, String columnName) {
        String sql = "select comment from ct.os_column where schemaName = ? and tableName = ? and columnName = ?";
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        param.put(2, tableName);
        param.put(3, columnName);
        JsonObject jsonObject = pq.executeQuery(sql, param);
        return jsonObject != null ? (jsonObject.get("COMMENT").isJsonNull() ? "" : jsonObject.get("COMMENT").getAsString()) : "";
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    public List<Map<String, Object>> getColumnComment(String schemaName, String tableName) {
        String sql = "select comment, columnName from ct.os_column where schemaName = ? and tableName = ? ";
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        param.put(2, tableName);
        return pq.getListMap(sql, param);
    }

    /**
     * @param schemaName
     * @param tableName
     */
    @Override
    public void delColumnComment(String schemaName, String tableName) {
        List<JsonObject> tableColumns = this.metaService.getTableColumns(schemaName, tableName);
        for (JsonObject tableColumn : tableColumns) {
            String sql = "delete from ct.os_column where schemaName = ? and tableName = ? and columnName = ?";
            Map<Integer,Object> param = new HashMap<>();
            param.put(1, schemaName);
            param.put(2, tableName);
            param.put(3, tableColumn.get("COLUMN_NAME").getAsString());
            pq.execute(sql, param);
        }
    }
}

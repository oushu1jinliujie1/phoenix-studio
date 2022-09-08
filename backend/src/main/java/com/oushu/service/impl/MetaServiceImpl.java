package com.oushu.service.impl;

import com.google.gson.JsonObject;
import com.oushu.model.BasicTableParam;
import com.oushu.model.IdxParam;
import com.oushu.phoenix.jdbc.PhoenixQuery;
import com.oushu.service.MetaService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MetaServiceImpl implements MetaService {
    private PhoenixQuery pq = new PhoenixQuery();

    /**
     * @return
     */
    @Override
    public List<Map<String, Object>> getSchemaList() {
        String schemaListSql = "select distinct TABLE_SCHEM from system.catalog where TABLE_SCHEM is not null";
        return pq.getListMap(schemaListSql, new HashMap<>());
    }

    /**
     * @param schemaName
     * @return
     */
    @Override
    public long getTableNum(String schemaName) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        JsonObject jsonObject = pq.executeQuery("select count(1) as count from system.catalog " +
                "where TABLE_SCHEM = ? and TABLE_TYPE = 'u'", param);
        return jsonObject.get("COUNT").getAsLong();
    }

    /**
     * @param schemaName
     * @return
     */
    @Override
    public boolean delSchema(String schemaName) {
        String dropSql = "drop schema \"" + schemaName + "\"";
        return pq.execute(dropSql, new HashMap<>()) == 0;
    }

    /**
     * @param schemaName
     * @return
     */
    @Override
    public boolean schemaExist(String schemaName) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        JsonObject jsonObject = pq.executeQuery("select count(1) as count from system.catalog " +
                "where TABLE_SCHEM = ?", param);
        return jsonObject.get("COUNT").getAsLong() > 0;
    }

    /**
     * @param Tparam
     * @return
     */
    @Override
    public List<Map<String, Object>> getTableList(BasicTableParam Tparam) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, Tparam.getSchemaName());
        param.put(2, "%" + Tparam.getTableName() + "%");
        param.put(3, Tparam.getLimit());
        param.put(4, Tparam.getOffset());
        String tableListSql = "select TABLE_NAME from system.catalog " +
                " where TABLE_SCHEM = ? and TABLE_TYPE = 'u' and TABLE_NAME like ?" +
                " group by TABLE_NAME limit ? offset ?";
        return pq.getListMap(tableListSql, param);
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    public long getTableNumWithSearch(String schemaName, String tableName) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        param.put(2, "%" + tableName + "%");
        JsonObject jsonObject = pq.executeQuery("select count(1) as count from system.catalog " +
                "where TABLE_SCHEM = ? and TABLE_NAME like ? and TABLE_TYPE = 'u'", param);
        return jsonObject.get("COUNT").getAsLong();
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    public boolean tableExist(String schemaName, String tableName) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        param.put(2, tableName);
        JsonObject jsonObject = pq.executeQuery("select count(1) as count from system.catalog " +
                "where TABLE_SCHEM = ? AND TABLE_NAME = ?", param);
        return jsonObject.get("COUNT").getAsLong() > 0;
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    public boolean delTable(String schemaName, String tableName) {
        String dropSql = "drop table \"" + schemaName + "\"." + "\"" + tableName + "\"";
        return pq.execute(dropSql, new HashMap<>()) == 0;
    }

    /**
     * @return
     */
    @Override
    public List<Map<String, Object>> getIdxList(BasicTableParam tableParam) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, tableParam.getSchemaName());
        param.put(2, tableParam.getTableName());
        param.put(3, tableParam.getLimit());
        param.put(4, tableParam.getOffset());
        String sql = "SELECT COLUMN_FAMILY, INDEX_STATE " +
                "FROM SYSTEM.CATALOG " +
                "WHERE TABLE_SCHEM = ? AND TABLE_NAME = ? AND TABLE_TYPE = 'i' LIMIT ? OFFSET ?";
        return pq.getListMap(sql, param);
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    public String getIdxState(String schemaName, String indexName) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        param.put(2, indexName);
        String sql = "SELECT INDEX_STATE " +
                "FROM SYSTEM.CATALOG " +
                "WHERE TABLE_SCHEM = ? AND TABLE_NAME = ? AND TABLE_TYPE = 'i' LIMIT 1";
        JsonObject res = pq.executeQuery(sql, param);
        return res.get("INDEX_STATE").getAsString();
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    public List<JsonObject> getTableColumns(String schemaName, String tableName) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        param.put(2, tableName);
        String sql = "select table_schem,TABLE_NAME,COLUMN_NAME,KEY_SEQ,ORDINAL_POSITION " +
                "from system.catalog  " +
                "where table_schem = ? and TABLE_NAME = ? AND COLUMN_NAME IS NOT null  " +
                "ORDER BY KEY_SEQ, ORDINAL_POSITION";
        return pq.getList(sql, param);
    }

    /**
     * @param tableParam
     * @return
     */
    @Override
    public List<JsonObject> getTableColumnsWithLimit(BasicTableParam tableParam) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, tableParam.getSchemaName());
        param.put(2, tableParam.getTableName());
        param.put(3, tableParam.getLimit());
        param.put(4, tableParam.getOffset());
        String sql = "select table_schem,TABLE_NAME,COLUMN_NAME,KEY_SEQ,ORDINAL_POSITION " +
                "from system.catalog  " +
                "where table_schem = ? and TABLE_NAME = ? AND COLUMN_NAME IS NOT null  " +
                "ORDER BY KEY_SEQ, ORDINAL_POSITION  LIMIT ? OFFSET ?";
        return pq.getList(sql, param);
    }

    /**
     * @param param
     * @return
     */
    @Override
    public boolean delIdx(IdxParam param) {
        String sql = "drop index \""+param.getIndexName()+"\" on \""
                + param.getSchemaName() + "\".\"" + param.getTableName() + "\"";
        return pq.execute(sql, new HashMap<>()) == 0;
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    public JsonObject getTableInfo(String schemaName, String tableName) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        param.put(2, tableName);
        String sql = "SELECT COLUMN_COUNT, TABLE_NAME, SALT_BUCKETS FROM  SYSTEM.CATALOG " +
                "WHERE TABLE_SCHEM = ? AND TABLE_NAME = ? AND TABLE_TYPE = 'u'";
        return pq.executeQuery(sql, param);
    }
}
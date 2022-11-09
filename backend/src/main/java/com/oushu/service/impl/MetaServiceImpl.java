package com.oushu.service.impl;

import com.google.gson.JsonObject;
import com.oushu.config.Studio;
import com.oushu.model.BasicTableParam;
import com.oushu.model.Column;
import com.oushu.model.IdxParam;
import com.oushu.phoenix.jdbc.PhoenixQuery;
import com.oushu.service.MetaService;
import com.oushu.service.SqlService;
import com.oushu.util.MyJDBCType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MetaServiceImpl implements MetaService {

    @Autowired
    private PhoenixQuery pq;

    @Autowired
    private SqlService sqlService;

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
     * @return
     */
    @Override
    public List<Map<String, Object>> getALLTableList(String schemaName) {
        String tableListSql = "select TABLE_NAME, TABLE_SCHEM from system.catalog " +
                " where TABLE_SCHEM <> 'CT' and TABLE_TYPE = 'u' and TABLE_SCHEM = ? " +
                " group by TABLE_NAME, TABLE_SCHEM ";
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
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
        this.sqlService.delTableComment(schemaName, tableName);
        this.sqlService.delColumnComment(schemaName, tableName);
        String dropSql = "drop table \"" + schemaName + "\"." + "\"" + tableName + "\"";
        return pq.execute(dropSql, new HashMap<>()) == 0;
    }

    /**
     * @param tableParam
     * @return
     */
    @Override
    public long getIdxCount(BasicTableParam tableParam) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, tableParam.getSchemaName());
        param.put(2, tableParam.getTableName());
        String sql = "SELECT COUNT(1) AS COUNT " +
                "FROM SYSTEM.CATALOG " +
                "WHERE TABLE_SCHEM = ? AND TABLE_NAME = ? AND TABLE_TYPE = 'i' ";
        JsonObject jsonObject = pq.executeQuery(sql, param);
        return jsonObject.get("COUNT").getAsLong();
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
    public long getTableColumnCount(String schemaName, String tableName) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        param.put(2, tableName);
        String sql = "select COUNT(1) AS COUNT " +
                "from system.catalog  " +
                "where table_schem = ? and TABLE_NAME = ? AND COLUMN_NAME IS NOT null  ";
        return pq.executeQuery(sql, param).get("COUNT").getAsLong();
    }

    /**
     * Deprecated
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    //Deprecated
    public List<JsonObject> getTableColumns(String schemaName, String tableName) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        param.put(2, tableName);
        String sql = "select table_schem,TABLE_NAME,COLUMN_NAME,COLUMN_FAMILY,KEY_SEQ,ORDINAL_POSITION,DATA_TYPE,COLUMN_SIZE,DECIMAL_DIGITS " +
                "from system.catalog  " +
                "where table_schem = ? and TABLE_NAME = ? AND COLUMN_NAME IS NOT null  " +
                "ORDER BY KEY_SEQ, ORDINAL_POSITION";
        return pq.getList(sql, param);
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    public List<JsonObject> getTableColumnsWithComment(String schemaName, String tableName) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        param.put(2, tableName);
        String sql = "select table_schem,TABLE_NAME,COLUMN_NAME,COLUMN_FAMILY,KEY_SEQ,ORDINAL_POSITION,DATA_TYPE,COLUMN_SIZE,DECIMAL_DIGITS " +
                "from system.catalog  " +
                "where table_schem = ? and TABLE_NAME = ? AND COLUMN_NAME IS NOT null  " +
                "ORDER BY KEY_SEQ, ORDINAL_POSITION";
        List<JsonObject> list = pq.getList(sql, param);
        for (int i = 0; i < list.size(); i++) {
            JsonObject jsonObject = list.get(i);
            int data_type = jsonObject.get("DATA_TYPE").getAsInt();
            String name = MyJDBCType.valueOf(data_type).name();
            jsonObject.addProperty("DATA_TYPE_NAME", name);
        }
        List<Map<String, Object>> columnComments = this.sqlService.getColumnComment(schemaName, tableName);
        for (JsonObject jsonObject : list) {
            Optional<Map<String, Object>> current = columnComments.stream().filter(item ->
                    item.getOrDefault("COLUMNNAME", "").toString().equals(
                            jsonObject.get("COLUMN_NAME").getAsString())).findFirst();
            if (current.isPresent()){
                String columnComment = current.get().getOrDefault("COMMENT", "").toString();
                jsonObject.addProperty("COMMENT", columnComment);
            } else {
                jsonObject.addProperty("COMMENT", "");
            }
        }
        return list;
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    public List<JsonObject> getTablePKColumns(String schemaName, String tableName) {
        Map<Integer,Object> param = new HashMap<>();
        param.put(1, schemaName);
        param.put(2, tableName);
        String sql = "select table_schem,TABLE_NAME,COLUMN_NAME,COLUMN_FAMILY,KEY_SEQ,ORDINAL_POSITION,DATA_TYPE " +
                "from system.catalog  " +
                "where table_schem = ? and TABLE_NAME = ? AND COLUMN_NAME IS NOT null AND KEY_SEQ IS NOT null  " +
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
        String sql = "select table_schem,TABLE_NAME,COLUMN_NAME,COLUMN_FAMILY,KEY_SEQ,ORDINAL_POSITION,DATA_TYPE,COLUMN_SIZE,DECIMAL_DIGITS " +
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
        JsonObject result = pq.executeQuery(sql, param);
        String tableComment = this.sqlService.getTableComment(schemaName, tableName);
        result.addProperty("COMMENT", tableComment);
        return result;
    }

    /**
     * @param column
     * @return
     */
    @Override
    public boolean delColumn(Column column) {
        String sql = "alter table " + column.getQuoteName()
                + " drop column " + column.getColumnNameWithQuote();
        return pq.execute(sql, new HashMap<>()) >= 0;
    }

    /**
     * @param tables
     * @return
     */
    @Override
    public Map<String, Integer> getColumnDataType(String[] tables) {
        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < tables.length; i++) {
            String schemaName = tables[i].split("\\.")[0];
            String tableName = tables[i].split("\\.")[1];
            String sql = "select DATA_TYPE, COLUMN_NAME from SYSTEM.CATALOG " +
                    " where TABLE_SCHEM = ? and TABLE_NAME = ? and COLUMN_NAME is not null";
            Map<Integer, Object> param = new HashMap<>();
            param.put(1, schemaName);
            param.put(2, tableName);
            List<JsonObject> list = pq.getList(sql, param);
            for (JsonObject jsonObject : list) {
                result.put(tableName + "." + jsonObject.get("COLUMN_NAME").getAsString(),
                        jsonObject.get("DATA_TYPE").getAsInt());
            }
        }
        return result;
    }
}

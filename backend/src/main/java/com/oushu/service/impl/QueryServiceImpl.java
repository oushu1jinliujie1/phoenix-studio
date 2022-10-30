package com.oushu.service.impl;

import com.google.gson.JsonObject;
import com.oushu.model.*;
import com.oushu.phoenix.jdbc.PhoenixQuery;
import com.oushu.service.MetaService;
import com.oushu.service.QueryService;
import com.oushu.util.DataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QueryServiceImpl implements QueryService {

    private String metaTableName = "ct.os_meta";

    private PhoenixQuery pq = new PhoenixQuery();

    @Autowired
    private MetaService metaService;

    /**
     * @param meta
     * @return
     */
    @Override
    public boolean CheckValide(OsMeta meta) {
        Column[] columns = meta.getColumns();
        HashMap<String, Boolean> connectionMap = new HashMap<>();
        for (int i = 0; i < columns.length; i++) {
            Column item = columns[i];
            connectionMap.put(item.getColumnName(), true);
        }
        HashMap<String, Boolean> AllColumn = new HashMap<>();
        TableName[] tableNames = meta.getTableNames();
        for (int i = 0; i < tableNames.length; i++) {
            TableName tableName = tableNames[i];
            List<JsonObject> tableColumns = this.metaService.getTableColumns(tableName.getSchemaName(), tableName.getTableName());
            List<JsonObject> column_name = tableColumns.stream().filter(
                    item -> !connectionMap.getOrDefault(item.get("COLUMN_NAME").getAsString(), false)
            ).collect(Collectors.toList());
            for (int j = 0; j < column_name.size(); j++) {
                String current_column_name = column_name.get(j).get("COLUMN_NAME").getAsString();
                if (!AllColumn.getOrDefault(current_column_name, false)){
                    AllColumn.put(current_column_name, true);
                } else {
                    return false;
                }
            }
        }
        return true;
    }

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
     * @param param
     * @return
     */
    @Override
    public long getQueryTableCount(QueryTableName param) {
        Map<Integer,Object> sqlParam = new HashMap<>();
        int paramNum = 1;
        String sql = "select count(1) as count from " + metaTableName;
        if (param.getQueryName() != null && !param.getQueryName().equals("")){
            sql += " where queryName like ? ";
            sqlParam.put(paramNum++, "%" + param.getQueryName() + "%");
        }
        JsonObject result = pq.executeQuery(sql, sqlParam);
        return result.get("COUNT").getAsLong();
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

    /**
     * @param tableName
     * @return
     */
    @Override
    public List<Map<String, Object>> querySearchTableData(SearchTableDataRequest tableName) {

        // 查询元数据
        String sql = "select * from " + this.metaTableName
                + " where QUERYNAME = ?";
        Map<Integer,Object> sqlParam = new HashMap<>();
        sqlParam.put(1, tableName.getSearchTableName());
        JsonObject queryTableInfo = pq.executeQuery(sql, sqlParam);
        String connections = queryTableInfo.get("CONNECTION").getAsString();
        String[] tables = queryTableInfo.get("TABLENAMES").getAsString().split(",");
        Map<String, Integer> columnDataType = metaService.getColumnDataType(tables);
        // 第1次查询，查询关联键
        String select = getSelectForPK(connections, columnDataType, tableName.getTableName());
        String conditionForPK = getConditionForPK(tableName.getSearchValue(),
                tableName.getSchemaName(), tableName.getTableName(), columnDataType);
        String keyListSql = "select " + select
                + " from " + tableName.getQuoteName()
                + " where " + conditionForPK;
        List<JsonObject> keyList = pq.getList(keyListSql, new HashMap<>());
        // 第2次查询，查询最终结果
        String finallySelectSql = tableName.getFinallySelectSql();
        List<TableName> tableNames = getTableNames(tables);
        List<String> temp = tableNames.stream().map(item -> item.getQuoteName()).collect(Collectors.toList());
        String finallyFromSql = String.join(" , ", temp);
        String pkLinkCondition = getPKLinkCondition(tableNames, connections);
        String pkCondition = getPKCondition(tableNames, connections, keyList);
        String finallySql = "select " + finallySelectSql
                + " from " + finallyFromSql
                + " where " + pkLinkCondition + " AND " + pkCondition
                + " limit " + tableName.getLimit();
        return pq.getListMap(finallySql, new HashMap<>());
    }

    /**
     * @param tableName
     * @return
     */
    @Override
    public List<Map<String, Object>> querySearchTableDataWithPK(SearchTableDataRequest tableName) {
        // 查询元数据
        String sql = "select * from " + this.metaTableName
                + " where QUERYNAME = ?";
        Map<Integer,Object> sqlParam = new HashMap<>();
        sqlParam.put(1, tableName.getSearchTableName());
        JsonObject queryTableInfo = pq.executeQuery(sql, sqlParam);
        String connections = queryTableInfo.get("CONNECTION").getAsString();
        String[] tables = queryTableInfo.get("TABLENAMES").getAsString().split(",");
        Map<String, Integer> columnDataType = metaService.getColumnDataType(tables);

        // 查询最终结果
        String finallySelectSql = tableName.getFinallySelectSql();
        List<TableName> tableNames = getTableNames(tables);
        List<String> temp = tableNames.stream().map(item -> item.getQuoteName()).collect(Collectors.toList());
        String finallyFromSql = String.join(" , ", temp);
        String pkLinkCondition = getPKLinkCondition(tableNames, connections);
        String conditionForPK = getConditionForPK(tableName.getSearchValue(),
                tableName.getSchemaName(), tableName.getTableName(), columnDataType);
        String finallySql = "select " + finallySelectSql
                + " from " + finallyFromSql
                + " where " + pkLinkCondition + " AND " + conditionForPK
                + " limit " + tableName.getLimit();
        return pq.getListMap(finallySql, new HashMap<>());
    }

    /**
     * @param tableName
     * @return
     */
    @Override
    public List<Map<String, Object>> queryBasicTableData(SearchTableDataRequest tableName) {
        // 查询最终结果
        String[] tables = new String[]{tableName.getNameWithoutQuote()};
        Map<String, Integer> columnDataType = metaService.getColumnDataType(tables);
        String finallySelectSql = tableName.getFinallySelectSql();
        String finallyFromSql = tableName.getQuoteName();
        String conditionForPK = getConditionForPK(tableName.getSearchValue(),
                tableName.getSchemaName(), tableName.getTableName(), columnDataType);
        String finallySql = "select " + finallySelectSql
                + " from " + finallyFromSql
                + " where " + conditionForPK
                + " limit " + tableName.getLimit();
        return pq.getListMap(finallySql, new HashMap<>());
    }

    private String getPKLinkCondition(List<TableName> tableNames, String connections) {
        String[] attrs = connections.split(",");
        List<String> temp = new ArrayList<>();
        for (int i = 0; i < tableNames.size()-1; i++) {
            List<String> innerTemp = new ArrayList<>();
            for (int j = 0; j < attrs.length; j++) {
                innerTemp.add(tableNames.get(i).getQuoteNameWithColumnName(attrs[j]) + " = "
                + tableNames.get(i+1).getQuoteNameWithColumnName(attrs[j]));
            }
            temp.add(String.join(" and ", innerTemp));
        }
        return String.join(" and ", temp);
    }

    private String getPKCondition(List<TableName> tableNames, String connections, List<JsonObject> keyList) {
        List<String> temp = new ArrayList<>();
        for (JsonObject jsonObject : keyList) {
            temp.add(jsonObject.get("keyList").getAsString());
        }
        String keyCondition = "(" + String.join(", ", temp) + " )";
        temp.clear();

        for (TableName tableName : tableNames) {
            String tableWithKey = getTableWithKey(tableName, connections);
            temp.add("(" + tableWithKey + ") in " + keyCondition);
        }
        return String.join(" and ", temp);
    }

    private String getTableWithKey(TableName tableName, String connections) {
        List<String> temp = new ArrayList<>();
        String[] attrs = connections.split(",");
        for (String attr : attrs) {
            temp.add(tableName.getQuoteNameWithColumnName(attr));
        }
        return String.join(", ", temp);
    }

    private List<TableName> getTableNames(String[] tables){
        List<TableName> result = new ArrayList<>();
        for (int i = 0; i < tables.length; i++) {
            String schemaName = tables[i].split("\\.")[0];
            String tableName = tables[i].split("\\.")[1];
            TableName item = new TableName();
            item.setSchemaName(schemaName);
            item.setTableName(tableName);
            result.add(item);
        }
        return result;
    }

    private String getSelectForPK(String connections,
                                  Map<String, Integer> columnDataType, String tableName) {
        List<String> temp = new ArrayList<>();
        for (String columnName : connections.split(",")) {
            Integer dataType = columnDataType.getOrDefault(tableName + "." + columnName, Types.VARCHAR);
            if (DataType.isNumber(dataType)){
                temp.add("\"" + columnName + "\"");
            } else {
                temp.add("'\\'' || \"" + columnName + "\" || '\\''");
            }
        }
        String join = String.join(" || ',' || ", temp);
        return "'(' || " + join + "|| ')' as \"keyList\"";
    }

    private String getConditionForPK(Map<String, String> searchValue, String schemaName, String tableName,
                                     Map<String, Integer> columnDataType){
        List<String> temp = new ArrayList<>();
        for (String key : searchValue.keySet()) {
            if (!searchValue.get(key).isEmpty()){
                Integer dataType = columnDataType.getOrDefault(tableName + "." + key, Types.VARCHAR);
                if (DataType.isNumber(dataType)){
                    temp.add("\"" + schemaName + "\".\"" + tableName + "\".\"" + key + "\"" + " = " + searchValue.get(key));
                } else {
                    temp.add("\"" + schemaName + "\".\"" + tableName + "\".\"" + key + "\"" + " = '" + searchValue.get(key) + "'");
                }
            }
        }
        return String.join(" and ", temp);
    }
}

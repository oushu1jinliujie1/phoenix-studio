package com.oushu.service.impl;

import com.google.gson.Gson;
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

    private String metaConnectionsTableName = "ct.os_meta_connection";

    private PhoenixQuery pq = new PhoenixQuery();

    @Autowired
    private MetaService metaService;

    /**
     * @param meta
     * @return
     */
    @Override
    public boolean CheckValide(OsMeta meta) {
        List<Column[]> metaColumns = meta.getConnections();
        HashMap<String, Boolean> connectionMap = new HashMap<>();
        for (Column[] metaColumn : metaColumns) {
            for (Column column : metaColumn) {
                connectionMap.put(column.getColumnName(), true);
            }
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
        String upSertSql = "upsert into " + metaTableName + " values ( ?, ?, ?, ? )";
        pq.execute(upSertSql, param);
        List<String> listConnections = meta.getListConnections();
        for (int i = 0; i < listConnections.size(); i++) {
            param.clear();
            param.put(1, meta.getQueryName());
            param.put(2, i+1);
            param.put(3, listConnections.get(i));
            upSertSql = "upsert into " + metaConnectionsTableName + " values (?, ?, ?)";
            pq.execute(upSertSql, param);
        }
        return true;
    }

    /**
     * @return
     */
    @Override
    public List<Map<String, Object>> getALLQueryTable() {
        String sql = "select * from " + metaTableName;
        return pq.getListMap(sql, new HashMap<>());
    }

    /**
     * @param param
     * @return
     */
    @Override
    public List<MetaInfo> getQueryTableList(QueryTableName param) {
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
        List<Map<String, Object>> queryTables = pq.getListMap(sql, sqlParam);
        List<MetaInfo> result = new ArrayList<>();
        for (int i = 0; i < queryTables.size(); i++) {
            MetaInfo queryname = this.queryTableInfo(queryTables.get(i).get("QUERYNAME").toString());
            result.add(queryname);
        }
        return result;
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
        pq.execute(sql, sqlParam);
        sql = "delete from " + this.metaConnectionsTableName
                + " where queryName = ?";
        pq.execute(sql, sqlParam);
        return true;
    }

    //TODO 同步到查询jar包
    /**
     * @param queryTableName
     * @return
     */
    @Override
    public MetaInfo queryTableInfo(String queryTableName) {
        String sql = "select * from " + this.metaTableName
                + " where queryName = ?";
        Map<Integer,Object> sqlParam = new HashMap<>();
        sqlParam.put(1, queryTableName);
        JsonObject result = pq.executeQuery(sql, sqlParam);
        sql = "select * from " + this.metaConnectionsTableName
                + " where queryName = ? order by ORDERNUM";
        List<JsonObject> dataConnections = pq.getList(sql, sqlParam);
        List<Column[]> connections = new ArrayList<>();
        for (JsonObject dataConnection : dataConnections) {
            String attrConn = dataConnection.get("CONNECTIONS").getAsString();
            String[] column_str = attrConn.split(",");
            Column[] columnItem = new Column[column_str.length];
            for (int i = 0; i < column_str.length; i++) {
                String[] split = column_str[i].split("\\.");
                columnItem[i] = new Column(split[0], split[1], split[2]);
            }
            connections.add(columnItem);
        }
        result.add("CONNECTIONS", new Gson().toJsonTree(connections));
        return new Gson().fromJson(result, MetaInfo.class);
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
    public List<MetaInfo> getConnectedQueryTableList(TableName tableName) {
        String sql = "select * from " + this.metaTableName
                + " where TABLENAMES like ?";
        Map<Integer,Object> sqlParam = new HashMap<>();
        sqlParam.put(1, "%" + tableName.getNameWithoutQuote() + "%");
        List<Map<String, Object>> listMap = pq.getListMap(sql, sqlParam);
        List<MetaInfo> result = new ArrayList<>();
        for (int i = 0; i < listMap.size(); i++) {
            MetaInfo queryname = this.queryTableInfo(listMap.get(i).get("QUERYNAME").toString());
            result.add(queryname);
        }
        return result;
    }

    /**
     * @param tableName
     * @return
     */
    @Override
    public List<Map<String, Object>> querySearchTableData(SearchTableDataRequest tableName) {

        // 查询元数据
        MetaInfo metaInfo = this.queryTableInfo(tableName.getSearchTableName());
        String[] tables = metaInfo.getTableNames().split(",");
        Map<String, Integer> columnDataType = metaService.getColumnDataType(tables);
        List<TableName> tableNames = getTableNames(tables);
        List<MetaInfo.ColumnConnection[]> connections = metaInfo.getConnections();
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
        List<String> temp = tableNames.stream().map(item -> item.getQuoteName()).collect(Collectors.toList());
        String finallyFromSql = String.join(" , ", temp);
        String pkLinkCondition = getPKLinkCondition(connections);
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
        MetaInfo metaInfo = this.queryTableInfo(tableName.getSearchTableName());
        String[] tables = metaInfo.getTableNames().split(",");
        Map<String, Integer> columnDataType = metaService.getColumnDataType(tables);
        List<TableName> tableNames = getTableNames(tables);
        List<MetaInfo.ColumnConnection[]> connections = metaInfo.getConnections();

        // 查询最终结果
        String finallySelectSql = tableName.getFinallySelectSql();
        List<String> temp = tableNames.stream().map(item -> item.getQuoteName()).collect(Collectors.toList());
        String finallyFromSql = String.join(" , ", temp);
        String pkLinkCondition = getPKLinkCondition(connections);
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

    //TODO 同步到查询jar包
    private String getPKLinkCondition(List<MetaInfo.ColumnConnection[]> connections) {
        List<String> temp = new ArrayList<>();
        for (int i = 0; i < connections.size(); i++) {
            MetaInfo.ColumnConnection[] columnConnections = connections.get(i);
            List<String> innerTemp = new ArrayList<>();
            for (int j = 0; j < columnConnections.length-1; j++) {
                innerTemp.add(columnConnections[j].getQuoteName() + " = " + columnConnections[j+1].getQuoteName());
            }
            temp.add(String.join(" and ", innerTemp));
        }
        return String.join(" and ", temp);
    }

    //TODO 同步到查询jar包
    private String getPKCondition(List<TableName> tableNames, List<MetaInfo.ColumnConnection[]> connections,
                                  List<JsonObject> keyList) {
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

    private String getTableWithKey(TableName tableName, List<MetaInfo.ColumnConnection[]> connections) {
        List<String> temp = new ArrayList<>();
        for (MetaInfo.ColumnConnection[] connection : connections) {
            for (MetaInfo.ColumnConnection columnConnection : connection) {
                if (columnConnection.getTableName().equals(tableName.getTableName()) &&
                        columnConnection.getSchemaName().equals(tableName.getSchemaName())){
                    temp.add(columnConnection.getQuoteName());
                }
            }
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

    private String getSelectForPK(List<MetaInfo.ColumnConnection[]> connections,
                                  Map<String, Integer> columnDataType, String tableName) {
        List<String> temp = new ArrayList<>();
        for (MetaInfo.ColumnConnection[] connection : connections) {
            Optional<MetaInfo.ColumnConnection> column = Arrays.stream(connection).filter(item -> item.getTableName().equals(tableName)).findFirst();
            String columnName = column.get().getColumnName();
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

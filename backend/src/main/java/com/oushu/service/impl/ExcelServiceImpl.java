package com.oushu.service.impl;

import com.alibaba.excel.util.ListUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.oushu.model.MetaInfo;
import com.oushu.service.ExcelService;
import com.oushu.service.MetaService;
import com.oushu.service.QueryService;
import com.oushu.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private MetaService metaService;

    @Autowired
    private QueryService queryService;

    @Override
    public List<List<String>> getBasicTableExcelHeader() {
        List<List<String>> list = ListUtils.newArrayList();
        String[] title = {"表名", "表备注", "split_on", "SALT_BUCKETS", "", "", ""};
        for (int i = 0; i < title.length; i++) {
            List<String> head = ListUtils.newArrayList();
            head.add(title[i]);
            list.add(head);
        }
        return list;
    }

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    public List<List<Object>> getBasicTableExcelData(String schemaName, String tableName) {
        List<List<Object>> excelData = new ArrayList<>();
        JsonObject tableInfo = this.metaService.getTableInfo(schemaName, tableName);
        List<Object> titleContent = new ArrayList<>();
        titleContent.add(tableInfo.get("TABLE_NAME").getAsString());
        titleContent.add(tableInfo.get("COMMENT").getAsString());
        titleContent.add("");
        titleContent.add(tableInfo.get("SALT_BUCKETS").isJsonNull() ? "" : tableInfo.get("SALT_BUCKETS").getAsString());
        excelData.add(titleContent);
        //列信息
        List<Object> columnTitle = new ArrayList<>();
        String[] columnChineseName = {"列名", "列备注", "列族", "数据类型", "长度", "精度", "是否为主键"};
        for (String item : columnChineseName) {
            columnTitle.add(item);
        }
        excelData.add(columnTitle);

        List<JsonObject> tableColumns = this.metaService.getTableColumnsWithComment(schemaName, tableName);
        for (JsonObject tableColumn : tableColumns) {
            List<Object> columnContent = new ArrayList<>();
            String[] columnNames = {"COLUMN_NAME", "COMMENT", "COLUMN_FAMILY", "DATA_TYPE_NAME",
                    "COLUMN_SIZE", "DECIMAL_DIGITS"};
            for (String columnName : columnNames) {
                columnContent.add(tableColumn.get(columnName).isJsonNull() ? "" :
                        tableColumn.get(columnName).getAsString());
            }
            JsonElement key_seq = tableColumn.get("KEY_SEQ");
            boolean is_key = key_seq.isJsonNull() ? false : key_seq.getAsLong() > 0 ? true : false;
            columnContent.add(is_key);

            excelData.add(columnContent);
        }
        return excelData;
    }


    @Override
    public List<List<String>> getQueryTableExcelHeader(int length) {
        List<List<String>> list = ListUtils.newArrayList();
        List<String> title = new ArrayList<>();
        title.add("查询表名");
        title.add("查询表中文名");
        title.add("查询表描述");
        for (int i = 0; i < length - title.size(); i++) {
            title.add("");
        }
        for (int i = 0; i < title.size(); i++) {
            List<String> head = ListUtils.newArrayList();
            head.add(title.get(i));
            list.add(head);
        }
        return list;
    }

    /**
     * @param queryName
     * @return
     */
    @Override
    public List<List<Object>> getQueryTableExcelData(String queryName) {
        MetaInfo metaInfo = this.queryService.queryTableInfo(queryName);
        List<List<Object>> excelData = new ArrayList<>();
        List<Object> titleContent = new ArrayList<>();
        titleContent.add(metaInfo.getQueryName());
        titleContent.add(metaInfo.getChineseName());
        titleContent.add(metaInfo.getDescription());
        // 第2行
        excelData.add(titleContent);
        String[] tableNames = metaInfo.getTableNames().split(",");
        List<Object> title = new ArrayList<>();
        for (String tableName : tableNames) {
            title.add(tableName);
        }
        // 第3行
        excelData.add(title);

        List<MetaInfo.ColumnConnection[]> connections = metaInfo.getConnections();
        for (MetaInfo.ColumnConnection[] connection : connections) {
            titleContent = new ArrayList<>();
            for (MetaInfo.ColumnConnection columnConnection : connection) {
                titleContent.add(columnConnection.getColumnName());
            }
            excelData.add(titleContent);
        }
        return excelData;
    }
}

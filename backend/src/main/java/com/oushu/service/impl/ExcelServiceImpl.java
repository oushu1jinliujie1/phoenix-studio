package com.oushu.service.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.oushu.service.ExcelService;
import com.oushu.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private MetaService metaService;

    /**
     * @param schemaName
     * @param tableName
     * @return
     */
    @Override
    public List<List<Object>> getBasicTableExcelData(String schemaName, String tableName) {
        List<List<Object>> excelData = new ArrayList<>();
        List<Object> title = new ArrayList<>();
        title.add("表名");
        title.add("表备注");
        title.add("split_on");
        title.add("SALT_BUCKETS");
        excelData.add(title);
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
            String is_key = key_seq.isJsonNull() ? "FALSE" : key_seq.getAsLong() > 0 ? "TRUE" : "FALSE";
            columnContent.add(is_key);

            excelData.add(columnContent);
        }
        return excelData;
    }
}

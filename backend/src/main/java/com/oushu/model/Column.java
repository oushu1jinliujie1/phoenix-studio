package com.oushu.model;

import com.oushu.util.CommonError;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Column extends TableName{
    private String columnName;
    private String chineseName;
    private String comment;
    private String dataType;
    private String familyName;
    private int scale;
    private int precision;
    private boolean pk;

    public Column(String schemaName, String tableName, String columnName) {
        this.setSchemaName(schemaName);
        this.setTableName(tableName);
        this.columnName = columnName;
    }

    public CommonError checkValide(){
        if (this.isPk() && this.familyName != null && this.familyName.length() > 0 && !this.familyName.equals("0")){
            return new CommonError(true, "主键列" + this.columnName + "不能设置列族:" + familyName);
        }
        return CommonError.NonError;
    }

    public String getColumnSql(){
        String columnSql = "";
        // 0是主键的列族
        if (this.familyName != null
                && this.familyName.length() > 0
                && !this.familyName.equals("0")
                && !this.isPk()){
            columnSql = "\"" + this.familyName + "\".";
        }
        columnSql += "\"" + this.columnName + "\" ";
        List<String> temp = new ArrayList<>();
        if (scale > 0){
            temp.add(String.valueOf(scale));
        }
        if (precision > 0){
            temp.add(String.valueOf(precision));
        }
        String size = String.join(",", temp);
        if (size.length() > 0){
            size = " (" + size + ") ";
        }
        String dataType = this.dataType + size;
        columnSql += dataType;
        if (this.isPk()){
            columnSql += " NOT NULL ";
        }
        return columnSql;
    }
    public String getColumnSqlWithPK(){
        String sql = getColumnSql();
        if (this.pk){
            sql += " PRIMARY KEY";
        }
        return sql;
    }

    public String getColumnNameWithQuote(){
        if (this.familyName != null && !this.familyName.equals("")){
            return "\"" + this.familyName + "\".\"" + this.getColumnName() + "\"";
        }
        return "\"" + this.getColumnName() + "\"";
    }

    public String getFullPathColumnName(){
        return this.getNameWithoutQuote() + "." + this.columnName;
    }
}

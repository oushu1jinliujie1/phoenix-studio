package com.oushu.model;

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

    public String getColumnSql(){
        String columnSql = "";
        if (this.familyName != null && this.familyName.length() > 0){
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
}

package com.oushu.model;

import com.oushu.util.CommonError;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CreateTableRequest {
    private String schemaName;
    private String tableName;
    private String comment;
    private String splitOn;
    private int saltBuckets;
    private Column[] columns;

    public CommonError checkValide(){
        for (int i = 0; i < this.columns.length; i++) {
            CommonError commonError = this.columns[i].checkValide();
            if (commonError != CommonError.NonError){
                return commonError;
            }
        }
        return CommonError.NonError;
    }

    public String getCreateSql() {
        String sql = "CREATE TABLE \"" + this.schemaName + "\".\"" + this.tableName + "\"";
        List<String> columnSql = new ArrayList<>();
        for (int i = 0; i < this.columns.length; i++) {
            columnSql.add(this.columns[i].getColumnSql());
        }
        List<String> collect = Arrays.stream(this.columns)
                .filter(item -> item.isPk())
                .map(item -> item.getColumnNameWithQuote())
                .collect(Collectors.toList());
        columnSql.add("CONSTRAINT pk PRIMARY KEY( " + String.join(" , ", collect) + ")");
        sql += "(" + String.join(" , ", columnSql) + ") ";
        if (this.saltBuckets > 0){
            sql += " SALT_BUCKETS = " + this.saltBuckets;
        }
        if (this.splitOn != null && this.splitOn.length() > 0){
            sql += " SPLIT ON ( " + this.splitOn + ")";
        }
        return sql;
    }
}



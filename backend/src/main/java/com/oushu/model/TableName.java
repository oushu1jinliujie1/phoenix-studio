package com.oushu.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableName {
    private String schemaName;
    private String tableName;

    public String getQuoteName(){
        return "\"" + schemaName + "\".\"" + tableName + "\"";
    }

    public String getNameWithoutQuote(){
        return schemaName + "." + tableName;
    }
}

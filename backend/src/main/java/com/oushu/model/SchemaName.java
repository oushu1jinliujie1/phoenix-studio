package com.oushu.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchemaName {
    private String schemaName;

    public String getCreateSchemaSql(){
        return "CREATE SCHEMA IF NOT EXISTS \"" + this.getSchemaName() + "\"";
    }
}

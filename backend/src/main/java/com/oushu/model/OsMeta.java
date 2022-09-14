package com.oushu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OsMeta {
    private String queryName;
    private String chineseName;
    private String description;
    private TableName[] tableNames;
    private Column[] columns;
}

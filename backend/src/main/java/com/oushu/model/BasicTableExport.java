package com.oushu.model;

import lombok.Data;

import java.util.List;

@Data
public class BasicTableExport {
    private String schemaName;
    private List<String> tableNames;
}

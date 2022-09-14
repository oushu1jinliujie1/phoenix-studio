package com.oushu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QueryTableName {
    private String queryName;
    private int offset;
    private int limit;
}

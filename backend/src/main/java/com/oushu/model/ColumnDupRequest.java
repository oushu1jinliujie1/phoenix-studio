package com.oushu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ColumnDupRequest extends BasicTableParam{
    private String columnName;
}

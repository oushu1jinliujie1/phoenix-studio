package com.oushu.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicTableParam extends TableName {
    private int offset;
    private int limit;
}

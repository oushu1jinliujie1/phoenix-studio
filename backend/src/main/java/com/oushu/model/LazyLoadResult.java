package com.oushu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LazyLoadResult {
    private long count;
    private Object data;

    public LazyLoadResult(long count, Object data) {
        this.count = count;
        this.data = data;
    }
}

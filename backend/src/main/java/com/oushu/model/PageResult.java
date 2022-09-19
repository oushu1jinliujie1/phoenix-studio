package com.oushu.model;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PageResult {
    private long count;
    private Object data;

    public PageResult(long count, Object data) {
        this.count = count;
        this.data = data;
    }
}

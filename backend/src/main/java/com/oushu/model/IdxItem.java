package com.oushu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class IdxItem {
    private String indexName;
    private List<String> idxAttrs;
    private List<String> includesAttrs;
    private String indexState;

    public IdxItem() {
    }
}

package com.oushu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class OsMeta {
    private String queryName;
    private String chineseName;
    private String description;
    private TableName[] tableNames;
    private List<Column[]> connections;

    public List<String> getListConnections(){
        List<String> result = new ArrayList<>();
        for (Column[] connection : connections) {
            List<String> temp = new ArrayList<>();
            for (Column column : connection) {
                temp.add(column.getFullPathColumnName());
            }
            result.add(String.join(",", temp));
        }
        return result;
    }

}

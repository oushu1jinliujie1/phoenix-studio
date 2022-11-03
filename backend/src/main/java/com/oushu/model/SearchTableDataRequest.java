package com.oushu.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class SearchTableDataRequest extends TableName {
    private String searchTableName;
    private String secondaryIndex;
    private Map<String, String> searchValue;
    private List<Column> returnColumns;
    private int limit;

    public String getFinallySelectSql() {
        List<String> temp = this.returnColumns.stream()
                .map(item -> {
                    return item.getQuoteName() + "." + item.getColumnNameWithQuote() + " AS "
                            + "\"" + item.getColumnName() + "(" + item.getDataType() + ")" + "\"";
                })
                .collect(Collectors.toList());
        return String.join(" , ", temp);
    }
}

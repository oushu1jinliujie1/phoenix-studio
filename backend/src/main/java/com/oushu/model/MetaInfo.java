package com.oushu.model;


import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MetaInfo {

    @SerializedName("QUERYNAME")
    private String queryName;
    @SerializedName("CHINESENAME")
    private String chineseName;
    @SerializedName("DESCRIPTION")
    private String description;
    @SerializedName("TABLENAMES")
    private String tableNames;
    @SerializedName("CONNECTIONS")
    private List<ColumnConnection[]> connections;


    @Getter
    @Setter
    public static class ColumnConnection{
        private String schemaName;
        private String tableName;
        private String columnName;

        public String getQuoteName(){
            return "\"" + schemaName + "\".\""
                    + tableName + "\".\""
                    + columnName + "\"" ;
        }
    }
}

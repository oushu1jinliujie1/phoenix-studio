package com.oushu.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@Setter
public class Create2IdxRequest extends TableName {
    private String indexName;
    private String[] attrs;
    private String[] includesAttrs;

    public boolean checkValid(){
        if (attrs.length == 0){
            return false;
        }
        for (int i = 0; i < attrs.length; i++) {
            int finalI = i;
            if (Arrays.stream(includesAttrs).filter(item -> item.equals(attrs[finalI])).count() > 0){
                return false;
            }
        }
        return true;
    }

    public String getSql(){
        String attr = Arrays.stream(attrs)
                .map(item -> "\"" + item + "\"")
                .collect(Collectors.joining(","));
        String sql = "CREATE INDEX IF NOT EXISTS \"" +
                this.getIndexName() + "\" ON " + this.getQuoteName() + "(" + attr + ")";
        if (includesAttrs.length > 0){
            String includesAttr = Arrays.stream(includesAttrs)
                    .map(item -> "\"" + item + "\"")
                    .collect(Collectors.joining(","));
            sql += " INCLUDE( " + includesAttr + ") ";
        }
        sql += " ASYNC";
        return sql;
    }

    public String getRunIndexShell(){
        Configuration entries = HBaseConfiguration.create();
        String znode = entries.get("zookeeper.znode.parent");
        return "hbase org.apache.phoenix.mapreduce.index.IndexTool --schema \\\"" + this.getSchemaName() + "\\\" " +
                "--data-table \\\"" + this.getTableName() + "\\\" --index-table \\\"" + this.getIndexName() + "\\\" " +
                "--output-path " + znode + "/data/" + this.getSchemaName() + "/" + this.getIndexName() + " --run-foreground";
    }
}

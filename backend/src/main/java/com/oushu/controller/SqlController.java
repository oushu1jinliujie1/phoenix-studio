package com.oushu.controller;

import com.google.gson.JsonObject;
import com.oushu.model.*;
import com.oushu.service.MetaService;
import com.oushu.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SqlController {

    @Autowired
    private SqlService sqlService;

    @Autowired
    private MetaService metaService;

    @PostMapping("/sql/execute")
    public ResponseModel execSQL(@RequestBody ExecSql sql){
        boolean hasSuccess = this.sqlService.execSQL(sql.getSql());
        ResponseModel responseModel = new ResponseModel();
        if (hasSuccess){
            return responseModel.success();
        } else {
            return responseModel.failure();
        }
    }

    @PostMapping("/schema/sql/create")
    public ResponseModel createSchemaSQL(@RequestBody SchemaName name){
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(name.getCreateSchemaSql());
    }

    @PostMapping("/basic_table/sql/create")
    public ResponseModel createSchemaSQL(@RequestBody CreateTableRequest param){
        String sql = param.getCreateSql();
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(sql);
    }

    @PostMapping("/basic_table/create")
    public ResponseModel createBasicTable(@RequestBody CreateTableRequest param){
        ResponseModel responseModel = new ResponseModel();
        String sql = param.getCreateSql();
        boolean success = this.sqlService.execSQL(sql);
        if (!success){
            return responseModel.failure("创建失败");
        }
        success = this.sqlService.saveTableComment(param);
        if (!success){
            return responseModel.failure("保存表的comment失败");
        }
        return responseModel.success("创建成功");
    }

    @PostMapping("/basic_table/import")
    public ResponseModel importBasicTable(@RequestBody List<CreateTableRequest> params){
        ResponseModel responseModel = new ResponseModel();
        for (CreateTableRequest param : params) {
            boolean isExist = this.metaService.tableExist(param.getSchemaName(), param.getTableName());
            if (isExist){
                this.sqlService.delTableComment(param.getSchemaName(), param.getTableName());
                this.sqlService.delColumnComment(param.getSchemaName(), param.getTableName());
                List<JsonObject> tableColumns = this.metaService.getTableColumns(param.getSchemaName(), param.getTableName());
                List<String> column_name = tableColumns.stream()
                        .map(item -> item.get("COLUMN_NAME").getAsString())
                        .collect(Collectors.toList());
                Column[] importColumns = param.getColumns();
                for (Column column : importColumns) {
                    if (!column_name.contains(column.getColumnName())){
                        String sql = "ALTER TABLE " + column.getQuoteName() + " ADD "
                                + column.getColumnSqlWithPK();
                        this.sqlService.execSQL(sql);
                    }
                }
            } else {
                String sql = param.getCreateSql();
                this.sqlService.execSQL(sql);
            }
            this.sqlService.saveTableComment(param);
        }
        return responseModel.success("批量导入成功");
    }

    @PostMapping("/secondary_index/create")
    public ResponseModel create2IndexSQL(@RequestBody Create2IdxRequest param){
        ResponseModel responseModel = new ResponseModel();
        boolean valid = param.checkValid();
        if (!valid){
            responseModel.failure("参数非法");
        }
        String sql = param.getSql();
        return responseModel.success(sql);
    }

    @PostMapping("/basic_table/sql/column_create")
    public ResponseModel addColumnSQL(@RequestBody Column column){
        ResponseModel responseModel = new ResponseModel();
        String sql = "ALTER TABLE " + column.getQuoteName() + " ADD "
                + column.getColumnSqlWithPK();
        return responseModel.success(sql);
    }
}

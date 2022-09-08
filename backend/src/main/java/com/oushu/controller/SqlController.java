package com.oushu.controller;

import com.oushu.model.*;
import com.oushu.service.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SqlController {

    @Autowired
    private SqlService sqlService;

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
        return responseModel.success("CREATE SCHEMA IF NOT EXISTS \"" + name.getSchemaName() + "\"");
    }

    @PostMapping("/basic_table/sql/create")
    public ResponseModel createSchemaSQL(@RequestBody CreateTableRequest param){
        String sql = param.getCreateSql();
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(sql);
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

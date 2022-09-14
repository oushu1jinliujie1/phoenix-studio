package com.oushu.controller;

import com.google.gson.JsonObject;
import com.oushu.model.*;
import com.oushu.service.MetaService;
import com.oushu.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class QueryController {

    @Autowired
    private QueryService queryService;

    @PostMapping("/search_table/create")
    public ResponseModel createQueryTable(@RequestBody OsMeta meta){
        ResponseModel responseModel = new ResponseModel();
        boolean hasSuccess = this.queryService.CreateQuery(meta);
        if (hasSuccess){
            return responseModel.success();
        } else {
            return responseModel.failure();
        }
    }

    @PostMapping("/search_table/list")
    public ResponseModel tableList(@RequestBody QueryTableName param){
        List<Map<String, Object>> queryTableList = this.queryService.getQueryTableList(param);
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(queryTableList);
    }

    @PostMapping("/search_table/duplicate")
    public ResponseModel queryTableDup(@RequestBody QueryTableName tableName){
        boolean tableExists = this.queryService.queryNameDup(tableName.getQueryName());
        ResponseModel responseModel = new ResponseModel();
        if (tableExists){
            return responseModel.failure("名称重复");
        } else {
            return responseModel.success();
        }
    }

    @DeleteMapping("/search_table/delete/{queryTableName}")
    public ResponseModel queryTableDup(@PathVariable("queryTableName") String queryTableName){
        boolean success = this.queryService.delQueryName(queryTableName);
        ResponseModel responseModel = new ResponseModel();
        if (success){
            return responseModel.failure("名称重复");
        } else {
            return responseModel.success();
        }
    }

    @GetMapping("/search_table/info/{queryTableName}")
    public ResponseModel queryTableInfo(@PathVariable("queryTableName") String queryTableName){
        JsonObject info = this.queryService.queryTableInfo(queryTableName);
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(info);
    }
}

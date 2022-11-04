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
        if (meta.getConnections().size() == 0){
            return responseModel.failure("没有关联键");
        }
        boolean valide = this.queryService.CheckValide(meta);
        if (!valide){
            return responseModel.failure("存在重复列，无法创建");
        }
        boolean hasSuccess = this.queryService.CreateQuery(meta);
        if (hasSuccess){
            return responseModel.success();
        } else {
            return responseModel.failure();
        }
    }

    @PostMapping("/search_table/import")
    public ResponseModel importQueryTable(@RequestBody List<OsMeta> metas){
        ResponseModel responseModel = new ResponseModel();
        for (OsMeta meta : metas) {
            boolean tableExists = this.queryService.queryNameDup(meta.getQueryName());
            if (tableExists){
                this.queryService.delQueryName(meta.getQueryName());
            }
            if (meta.getConnections().size() == 0){
                return responseModel.failure("没有关联键");
            }
            boolean valide = this.queryService.CheckValide(meta);
            if (!valide){
                return responseModel.failure("存在重复列，无法创建");
            }
            boolean hasSuccess = this.queryService.CreateQuery(meta);
            if (!hasSuccess){
                return responseModel.failure();
            }
        }
        return responseModel.success();
    }

    @PostMapping("/search_table/list")
    public ResponseModel tableList(@RequestBody QueryTableName param){
        List<MetaInfo> queryTableList = this.queryService.getQueryTableList(param);
        long queryTableCount = this.queryService.getQueryTableCount(param);
        LazyLoadResult lazyLoadResult = new LazyLoadResult(queryTableCount, queryTableList);
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(lazyLoadResult);
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
            return responseModel.success("删除成功");
        } else {
            return responseModel.failure("删除失败");
        }
    }

    @GetMapping("/search_table/info/{queryTableName}")
    public ResponseModel queryTableInfo(@PathVariable("queryTableName") String queryTableName){
        MetaInfo info = this.queryService.queryTableInfo(queryTableName);
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(info);
    }

    @PostMapping("/basic_table/connect_search_table/list")
    public ResponseModel tableDup(@RequestBody TableName tableName){
        List<MetaInfo> connectedQueryTableList = this.queryService.getConnectedQueryTableList(tableName);
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(connectedQueryTableList);
    }

    @PostMapping("/search_table/search/data")
    public ResponseModel tableData(@RequestBody SearchTableDataRequest tableName){
        List<Map<String, Object>> result = this.queryService.querySearchTableData(tableName);
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(result);
    }

    @PostMapping("/search_table/search/data/pk")
    public ResponseModel tableDataWithPK(@RequestBody SearchTableDataRequest tableName){
        List<Map<String, Object>> result = this.queryService.querySearchTableDataWithPK(tableName);
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(result);
    }

    @PostMapping("/basic_table/search/data")
    public ResponseModel basicTableData(@RequestBody SearchTableDataRequest tableName){
        List<Map<String, Object>> result = this.queryService.queryBasicTableData(tableName);
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(result);
    }
}

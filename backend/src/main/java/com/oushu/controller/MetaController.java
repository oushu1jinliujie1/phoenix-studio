package com.oushu.controller;

import com.google.gson.JsonObject;
import com.oushu.model.*;
import com.oushu.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class MetaController {

    @Autowired
    private MetaService metaService;

    @PostMapping("/schema/list")
    public ResponseModel schemaList(){
        List<Map<String, Object>> schemaList = this.metaService.getSchemaList();
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(schemaList);
    }

    @DeleteMapping("/schema/delete/{name}")
    public ResponseModel schemaDel(@PathVariable("name") String name){
        ResponseModel responseModel = new ResponseModel();
        long tableNum = this.metaService.getTableNum(name);
        if (tableNum > 0){
            return responseModel.failure(name + "下有资源，无法删除");
        }
        boolean hasSuccess = this.metaService.delSchema(name);
        if (hasSuccess){
            return responseModel.success();
        } else {
            return responseModel.failure();
        }
    }

    @PostMapping("/schema/duplicate")
    public ResponseModel schemaDup(@RequestBody SchemaName schemaName){
        boolean schemaExists = this.metaService.schemaExist(schemaName.getSchemaName());
        ResponseModel responseModel = new ResponseModel();
        if (schemaExists){
            return responseModel.failure("名称重复");
        } else {
            return responseModel.success();
        }
    }

    @PostMapping("/basic_table/list")
    public ResponseModel tableList(@RequestBody BasicTableParam param){
        long tableNumWithSearch = this.metaService.getTableNumWithSearch(
                param.getSchemaName(), param.getTableName());
        List<Map<String, Object>> tableList = this.metaService.getTableList(param);
        ResponseModel responseModel = new ResponseModel();
        LazyLoadResult lazyLoadResult = new LazyLoadResult(tableNumWithSearch, tableList);
        return responseModel.success(lazyLoadResult);
    }

    @PostMapping("/basic_table/duplicate")
    public ResponseModel tableDup(@RequestBody TableName tableName){
        boolean tableExists = this.metaService.tableExist(tableName.getSchemaName(), tableName.getTableName());
        ResponseModel responseModel = new ResponseModel();
        if (tableExists){
            return responseModel.failure("名称重复");
        } else {
            return responseModel.success();
        }
    }

    @DeleteMapping("/basic_table/delete/{schemaName}/{tableName}")
    public ResponseModel tableDel(@PathVariable("schemaName") String schemaName,
                                  @PathVariable("tableName") String tableName){
        ResponseModel responseModel = new ResponseModel();
        boolean hasSuccess = this.metaService.delTable(schemaName, tableName);
        if (hasSuccess){
            return responseModel.success();
        } else {
            return responseModel.failure();
        }
    }

    @PostMapping("/basic_table/secondary_index/list")
    public ResponseModel idxList(@RequestBody BasicTableParam param){
        ResponseModel responseModel = new ResponseModel();
        List<Map<String, Object>> idxList = this.metaService.getIdxList(param);
        List<IdxItem> result = new ArrayList<>();
        for (int i = 0; i < idxList.size(); i++) {
            IdxItem idxItem = new IdxItem();
            idxItem.setIndexName(idxList.get(i).get("COLUMN_FAMILY").toString());
            String idxState = this.metaService.getIdxState(param.getSchemaName(), idxItem.getIndexName());
            idxItem.setIndexState(idxState);
            // get table columns
            List<JsonObject> tableColumns = this.metaService.getTableColumns(param.getSchemaName(), idxItem.getIndexName());
            List<String> idxAttr = tableColumns.stream()
                    .filter(item -> !item.get("KEY_SEQ").isJsonNull())
                    .map(item -> item.get("COLUMN_NAME").getAsString().split(":")[1])
                    .collect(Collectors.toList());
            idxItem.setIdxAttrs(idxAttr);
            List<String> includesAttr = tableColumns.stream()
                    .filter(item -> item.get("KEY_SEQ").isJsonNull())
                    .map(item -> item.get("COLUMN_NAME").getAsString().split(":")[1])
                    .collect(Collectors.toList());
            idxItem.setIncludesAttrs(includesAttr);
            result.add(idxItem);
        }
        return responseModel.success(result);
    }

    @DeleteMapping("/secondary_index/delete")
    public ResponseModel indexDel(@RequestBody IdxParam param){
        ResponseModel responseModel = new ResponseModel();
        boolean hasSuccess = this.metaService.delIdx(param);
        if (hasSuccess){
            return responseModel.success();
        } else {
            return responseModel.failure();
        }
    }

    @PostMapping("/basic_table/details")
    public ResponseModel tableDetail(@RequestBody TableName tableName){
        JsonObject tableInfo = this.metaService.getTableInfo(tableName.getSchemaName(), tableName.getTableName());
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(tableInfo);
    }

    @PostMapping("/basic_table/columns")
    public ResponseModel tableColumns(@RequestBody BasicTableParam param){
        List<JsonObject> tableColumns = this.metaService.getTableColumnsWithLimit(param);
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(tableColumns.toArray());
    }

    @PostMapping("/basic_table/column/duplicate")
    public ResponseModel tableColumnDup(@RequestBody ColumnDupRequest column){
        List<JsonObject> tableColumns = this.metaService.getTableColumns(column.getSchemaName(), column.getTableName());
        long column_dup_count = tableColumns.stream()
                .filter(item -> item.get("COLUMN_NAME").getAsString().equals(column.getColumnName()))
                .count();
        ResponseModel responseModel = new ResponseModel();
        if (column_dup_count > 0){
            return responseModel.failure("名称重复");
        } else {
            return responseModel.success();
        }
    }
}

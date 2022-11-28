package com.oushu.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.gson.JsonObject;
import com.oushu.model.*;
import com.oushu.service.ExcelService;
import com.oushu.service.MetaService;
import com.oushu.service.QueryService;
import com.oushu.service.SqlService;
import com.oushu.util.CommonError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class SqlController {

    @Autowired
    private SqlService sqlService;

    @Autowired
    private MetaService metaService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private QueryService queryService;

    @PostMapping("/sql/execute")
    public ResponseModel execSQL(@RequestBody ExecSql sql){
        this.sqlService.execSQL(sql.getSql());
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success();
    }

    @PostMapping("/schema/sql/create")
    public ResponseModel createSchemaSQL(@RequestBody SchemaName name){
        ResponseModel responseModel = new ResponseModel();
        return responseModel.success(name.getCreateSchemaSql());
    }

    @PostMapping("/schema/create")
    public ResponseModel createSchema(@RequestBody SchemaName name){
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
        CommonError commonError = param.checkValide();
        if (commonError != CommonError.NonError){
            return responseModel.failure(commonError.getErrorString());
        }
        String sql = param.getCreateSql();
        this.sqlService.execSQL(sql);
        this.sqlService.saveTableComment(param);
        return responseModel.success("创建成功");
    }

    @PostMapping("/basic_table/import")
    public ResponseModel importBasicTable(@RequestBody List<CreateTableRequest> params){
        ResponseModel responseModel = new ResponseModel();
        for (CreateTableRequest param : params) {
            CommonError commonError = param.checkValide();
            if (commonError != CommonError.NonError){
                return responseModel.failure(commonError.getErrorString());
            }
        }
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

    //TODO delete
    @GetMapping("/basic_table/export")
    public void exportBasicTable(@RequestParam("schemaName") String schemaName, @RequestParam(name = "tableNames", required = false) String tableNames, HttpServletResponse response) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(schemaName + "模式下基础表的定义", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        List<String> tableNameList = null;
        if (tableNames != null & tableNames.length() > 0){
            tableNameList = Arrays.stream(tableNames.split(",")).collect(Collectors.toList());
        } else {
            List<Map<String, Object>> allTableList = this.metaService.getALLTableList(schemaName);
            tableNameList = allTableList.stream().map(item -> item.get("TABLE_NAME").toString()).collect(Collectors.toList());
        }

        try (ExcelWriter excelWriter = EasyExcel
                .write(response.getOutputStream())
                .head(this.excelService.getBasicTableExcelHeader())
                .useDefaultStyle(false)
                .build()) {
            for (int i = 0; i < tableNameList.size(); i++) {
                String tableName = tableNameList.get(i);
                //表信息
                List<List<Object>> excelData = this.excelService.getBasicTableExcelData(schemaName, tableName);
                WriteSheet writeSheet = EasyExcel.writerSheet(i, tableName).build();
                excelWriter.write(excelData, writeSheet);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/basic_table/export")
    public void exportBasicTable2(@RequestBody BasicTableExport params, HttpServletResponse response) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String schemaName = params.getSchemaName();
        List<String> tableNames = params.getTableNames();
        String fileName = URLEncoder.encode(schemaName + "模式下基础表的定义", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        List<String> tableNameList = null;
        if (tableNames != null & tableNames.size() > 0){
            tableNameList = tableNames;
        } else {
            List<Map<String, Object>> allTableList = this.metaService.getALLTableList(schemaName);
            tableNameList = allTableList.stream().map(item -> item.get("TABLE_NAME").toString()).collect(Collectors.toList());
        }

        try (ExcelWriter excelWriter = EasyExcel
                .write(response.getOutputStream())
                .head(this.excelService.getBasicTableExcelHeader())
                .useDefaultStyle(false)
                .build()) {
            for (int i = 0; i < tableNameList.size(); i++) {
                String tableName = tableNameList.get(i);
                //表信息
                List<List<Object>> excelData = this.excelService.getBasicTableExcelData(schemaName, tableName);
                WriteSheet writeSheet = EasyExcel.writerSheet(i, tableName).build();
                excelWriter.write(excelData, writeSheet);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/query_table/export")
    public void exportQueryTable(HttpServletResponse response) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("查询表的定义", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        List<Map<String, Object>> allQueryTable = this.queryService.getALLQueryTable();
        try (ExcelWriter excelWriter = EasyExcel
                .write(response.getOutputStream())
                .head(this.excelService.getQueryTableExcelHeader(10))
                .useDefaultStyle(false)
                .build()) {
            for (int i = 0; i < allQueryTable.size(); i++) {
                String queryName = allQueryTable.get(i).get("QUERYNAME").toString();
                //表信息
                List<List<Object>> excelData = this.excelService.getQueryTableExcelData(queryName);
                WriteSheet writeSheet = EasyExcel.writerSheet(i, queryName).build();
                excelWriter.write(excelData, writeSheet);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/query_table/export")
    public void exportQueryTable2(@RequestBody QueryTableExport params, HttpServletResponse response) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("查询表的定义", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        List<String> queryNames = params.getQueryNames();
        List<String> tableNameList = null;
        if (queryNames != null && queryNames.size() > 0){
            tableNameList = queryNames;
        } else {
            List<Map<String, Object>> allQueryTable = this.queryService.getALLQueryTable();
            tableNameList = allQueryTable.stream().map(item -> item.get("QUERYNAME").toString()).collect(Collectors.toList());
        }

        try (ExcelWriter excelWriter = EasyExcel
                .write(response.getOutputStream())
                .head(this.excelService.getQueryTableExcelHeader(10))
                .useDefaultStyle(false)
                .build()) {
            for (int i = 0; i < tableNameList.size(); i++) {
                String queryName = tableNameList.get(i);
                //表信息
                List<List<Object>> excelData = this.excelService.getQueryTableExcelData(queryName);
                WriteSheet writeSheet = EasyExcel.writerSheet(i, queryName).build();
                excelWriter.write(excelData, writeSheet);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    @PostMapping("/basic_table/column_create")
    public ResponseModel addColumn(@RequestBody Column column){
        ResponseModel responseModel = new ResponseModel();
        String sql = "ALTER TABLE " + column.getQuoteName() + " ADD "
                + column.getColumnSqlWithPK();
        this.sqlService.execSQL(sql);
        this.sqlService.saveColumnComment(column.getSchemaName(), column.getTableName(),
                column.getColumnName(), column.getComment());
        return responseModel.success("新增列成功");
    }
}

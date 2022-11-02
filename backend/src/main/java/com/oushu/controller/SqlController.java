package com.oushu.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.gson.JsonObject;
import com.oushu.model.*;
import com.oushu.service.ExcelService;
import com.oushu.service.MetaService;
import com.oushu.service.SqlService;
import com.sun.deploy.net.URLEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

    @GetMapping("/basic_table/export")
    public void exportBasicTable(HttpServletResponse response) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("基础表的定义", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        List<Map<String, Object>> allTableList = this.metaService.getALLTableList();
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build()) {
            for (int i = 0; i < allTableList.size(); i++) {
                String schemaName = allTableList.get(i).get("TABLE_SCHEM").toString();
                String tableName = allTableList.get(i).get("TABLE_NAME").toString();
                //表信息
                List<List<Object>> excelData = this.excelService.getBasicTableExcelData(schemaName, tableName);
                WriteSheet writeSheet = EasyExcel.writerSheet(i, tableName).build();
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
}

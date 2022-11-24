package com.oushu.controller;

import com.oushu.config.SpringContextUtils;
import com.oushu.config.Studio;
import com.oushu.model.Create2IdxRequest;
import com.oushu.model.ResponseModel;
import com.oushu.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RestController
@Slf4j
public class IndexController {

    @Autowired
    private Studio studio;

    @PostMapping("/index/run")
    public ResponseModel runIndex(@RequestBody Create2IdxRequest param) throws Exception {
        ResponseModel responseModel = new ResponseModel();
        String runIndexShell = studio.getCreate_index_shell() + " -s " + param.getSchemaName() + " -t " + param.getTableName() + " -i " + param.getIndexName();
        log.info(runIndexShell);
        String fileName = param.getSchemaName() + "-" + param.getTableName() + "-" + param.getIndexName() + ".log";
        Util.callScript(runIndexShell, fileName);
        return responseModel.success("正在创建索引");
    }

    @GetMapping("/index/log/{schemaName}/{tableName}/{indexName}")
    public ResponseModel getLog(@PathVariable String schemaName, @PathVariable String tableName, @PathVariable String indexName) throws Exception {
        ResponseModel responseModel = new ResponseModel();
        Studio studio = SpringContextUtils.getBean(Studio.class);
        String log_path = studio.getLog_path();
        String fileName = schemaName + "-" + tableName + "-" + indexName + ".log";
        String filePath = log_path + File.separator + fileName;
        FileInputStream fileInputStream = null;
        StringBuilder sb = new StringBuilder();
        try {
            fileInputStream = new FileInputStream(filePath);
            byte[] bytes = new byte[1024];
            int read = 0;
            while ((read = fileInputStream.read(bytes)) != -1){
                sb.append(new String(bytes, 0, read));
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        return responseModel.success(sb.toString());
    }
}

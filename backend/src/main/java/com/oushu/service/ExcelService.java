package com.oushu.service;

import java.util.List;

public interface ExcelService {

    List<List<String>> getBasicTableExcelHeader();

    List<List<Object>> getBasicTableExcelData(String schemaName, String tableName);

    List<List<String>> getQueryTableExcelHeader(int length);

    List<List<Object>> getQueryTableExcelData(String queryName);
}

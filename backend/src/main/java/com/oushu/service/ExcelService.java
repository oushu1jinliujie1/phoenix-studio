package com.oushu.service;

import java.util.List;

public interface ExcelService {

    List<List<Object>> getBasicTableExcelData(String schemaName, String tableName);
}

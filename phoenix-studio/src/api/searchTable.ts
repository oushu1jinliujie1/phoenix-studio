import http, { Response } from 'lava-fe-lib/lib-common/http'

/**
 * 获取查询表列表
 * @params
 * {
 *   schemaName: string,
 *   queryName: string,
 *   offset: number,
 *   limit: number
 * }
 */
 export const getSearchTableList = (
  {
    queryName,
    offset,
    limit 
  } : {
    queryName: string,
    offset: number,
    limit: number
  }
): Promise<Response<any>> => {
  return http.post('search_table/list', { queryName, offset, limit })
}

/**
 * 新建查询表
 * @params
 * {
 *   queryName:string,
 *   chineseName: string,
 *   description: string,
 *   tableNames: Array<{ schemaName: string, tableName: string }>,
 *   connections: Array<Array<{ columnName: string, tableName: string }>>
 * }
 */
 export const createSearchTable = (
  {
    queryName,
    chineseName,
    description,
    tableNames,
    connections
  } : {
    queryName:string,
    chineseName: string,
    description: string,
    tableNames: Array<{ schemaName: string, tableName: string }>,
    connections: Array<Array<{ columnName: string, tableName: string }>>
  }
): Promise<Response<any>> => {
  return http.post('search_table/create', { queryName, chineseName, description, tableNames, connections })
}

/**
 * 编辑查询表 (复用新建API，只要queryName相同，就会更新别的字段。queryName不允许修改)
 * @params
 * {
 *   queryName:string,
 *   chineseName: string,
 *   description: string,
 *   tableNames: Array<{ schemaName: string, tableName: string }>,
 *   connections: Array<Array<{ columnName: string, tableName: string }>>
 * }
 */
 export const editSearchTable = (
  {
    queryName,
    chineseName,
    description,
    tableNames,
    connections
  } : {
    queryName: string,
    chineseName: string,
    description: string,
    tableNames: Array<{ schemaName: string, tableName: string }>,
    connections: Array<Array<{ columnName: string, tableName: string }>>
  }
): Promise<Response<any>> => {
  return http.post('search_table/create', { queryName, chineseName, description, tableNames, connections })
}

/**
 * 导入查询表
 * @params
 * searchTables: Array<{
 *   queryName:string,
 *   chineseName: string,
 *   description: string,
 *   tableNames: Array<{ schemaName: string, tableName: string }>,
 *   connections: Array<Array<{ columnName: string, tableName: string }>>
 * }>
 */
 export const uploadSearchTable = (
  searchTables : Array<{
    queryName: string,
    chineseName: string,
    description: string,
    tableNames: Array<{ schemaName: string, tableName: string }>,
    connections: Array<Array<{ columnName: string, tableName: string }>>
  }>
): Promise<Response<any>> => {
  return http.post('search_table/import', searchTables)
}

/**
 * 导出查询表
 */
 export const exportSearchTable = (): Promise<any> => {
  return http.get('query_table/export', { responseType: 'blob' })
}

/**
 * 名称查重 查询表
 * @params queryName 查询表名称
 */
 export const duplicateSearchTable = (queryName: string): Promise<Response<any>> => {
  return http.post('search_table/duplicate', { queryName })
}

/**
 * 删除查询表
 * @params queryTableName 查询表名称
 */
 export const deleteSearchTable = (queryTableName: string): Promise<Response<any>> => {
  return http.delete(`search_table/delete/${queryTableName}`)
}

/**
 * 查询表详情
 * @params queryTableName 查询表名称
 */
 export const getSearchTableDetails = (queryTableName: string): Promise<Response<any>> => {
  return http.get(`search_table/info/${queryTableName}`)
}

/**
 * 查询表 数据查询
 * @params
 * {
 *   secondaryIndex: string | undefined,
 *   returnColumns: Array<{ columnName: string, dataType: string, schemaName: string | undefined, tableName: string | undefined }>
 *   limit: number | undefined,
 *   searchValue: { [key: string]: string },
 *   searchTableName: string | undefined,
 *   schemaName: string | undefined,
 *   tableName: string | undefined
 * }
 */
 export const filterData = (
  {
    secondaryIndex,
    returnColumns,
    limit,
    searchValue,
    searchTableName,
    schemaName,
    tableName
  } : {
    secondaryIndex: string | undefined,
    returnColumns: Array<{ columnName: string, dataType: string, schemaName: string | undefined, tableName: string | undefined }>
    limit: number | undefined,
    searchValue: { [key: string]: string },
    searchTableName: string | undefined,
    schemaName: string | undefined,
    tableName: string | undefined
  }
): Promise<Response<any>> => {
  return http.post('search_table/search/data', { secondaryIndex, returnColumns, limit, searchValue, searchTableName, schemaName, tableName })
}


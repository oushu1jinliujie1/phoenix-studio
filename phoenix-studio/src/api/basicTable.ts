import http, { Response } from 'lava-fe-lib/lib-common/http'

/**
 * 获取基础表列表
 * @params
 * {
 *   schemaName: string,
 *   tableName: string,
 *   offset: number,
 *   limit: number
 * }
 */
export const getTableList = (
  {
    schemaName,
    tableName,
    offset,
    limit 
  } : {
    schemaName:string,
    tableName: string,
    offset: number,
    limit: number
  }
): Promise<Response<any>> => {
  return http.post('basic_table/list', { schemaName, tableName, offset, limit })
}

/**
 * 新建基础表(获取SQL)
 * @params
 * {
 *   schemaName:string,
 *   tableName: string,
 *   splitOn: string,
 *   saltBuckets: number,
 *   columns: Array<{ familyName: string, columnName: string, dataType: string, scale: number, precision: number, pk: boolean }>
 * }
 */
export const getSqlForCreateTable = (
  {
    schemaName,
    tableName,
    splitOn,
    saltBuckets,
    columns
  } : {
    schemaName:string,
    tableName: string,
    splitOn: string,
    saltBuckets: number,
    columns: Array<{ familyName: string, columnName: string, dataType: string, scale: number, precision: number, pk: boolean }>
  }
): Promise<Response<any>> => {
  return http.post('basic_table/sql/create', { schemaName, tableName, splitOn, saltBuckets, columns })
}

/**
 * 新建基础表(获取SQL)
 * @params
 * {
 *   schemaName:string,
 *   tableName: string,
 *   comment: string,
 *   splitOn: string,
 *   saltBuckets: number,
 *   columns: Array<{ familyName: string, columnName: string, dataType: string, scale: number, precision: number, pk: boolean, comment: string }>
 * }
 */
 export const createTable = (
  {
    schemaName,
    tableName,
    comment,
    splitOn,
    saltBuckets,
    columns
  } : {
    schemaName:string,
    tableName: string,
    comment: string,
    splitOn: string,
    saltBuckets: number,
    columns: Array<{ familyName: string, columnName: string, dataType: string, scale: number, precision: number, pk: boolean, comment: string }>
  }
): Promise<Response<any>> => {
  return http.post('basic_table/create', { schemaName, tableName, comment, splitOn, saltBuckets, columns })
}


/**
 * 导入基础表
 * @params
 * basicTables: Array<{
 *   schemaName:string,
 *   tableName: string,
 *   comment: string,
 *   splitOn: string,
 *   saltBuckets: number,
 *   columns: Array<{ familyName: string, columnName: string, dataType: string, scale: number, precision: number, pk: boolean, comment: string }>
 * }>
 */
 export const uploadBasicTable = (
  basicTables: Array<{
    schemaName:string,
    tableName: string,
    comment: string,
    splitOn: string,
    saltBuckets: number,
    columns: Array<{ familyName: string, columnName: string, dataType: string, scale: number, precision: number, pk: boolean, comment: string }>
  }>
): Promise<Response<any>> => {
  return http.post('basic_table/import', basicTables)
}

/**
 * 导出基础表
 * @param schemaName Schema名称
 */
export const exportTable = (schemaName: string): Promise<any> => {
  return http.get(`basic_table/export?schemaName=${schemaName}`, { responseType: 'blob' })
}

/**
 * 删除基础表
 * @param schemaName Schema名称
 * @param tableName 基础表名称
 */
export const deleteTable = ({ schemaName, tableName } : { schemaName: string, tableName: string }): Promise<Response<any>> => {
  return http.delete(`basic_table/delete/${schemaName}/${tableName}`)
}

/**
 * 名称查重 基础表
 * @param schemaName Schema名称
 * @param tableName 基础表名称
 */
export const duplicateTable = ({ schemaName, tableName } : { schemaName: string, tableName: string }): Promise<Response<any>> => {
  return http.post('basic_table/duplicate', { schemaName, tableName })
}

/**
 * 基础表详情
 * @param schemaName Schema名称
 * @param tableName 基础表名称
 */
export const getTableDetails = ({ schemaName, tableName } : { schemaName: string, tableName: string }): Promise<Response<any>> => {
  return http.post('basic_table/details', { schemaName, tableName })
}

/**
 * 基础表的列
 * @params
 * {
 *   schemaName: string,
 *   tableName: string,
 *   offset: number,
 *   limit: number
 * }
 */
export const getColumnList = ({
  schemaName,
  tableName,
  offset,
  limit 
} : {
  schemaName:string,
  tableName: string,
  offset: number,
  limit: number
}): Promise<Response<any>> => {
  return http.post('basic_table/columns', { schemaName, tableName, offset, limit })
}

/**
 * 新建列(获取SQL)
 * @params
 * {
 *   schemaName:string,
 *   tableName: string,
 *   columnName: string,
 *   dataType: string,
 *   pk: boolean,
 *   scale: number,
 *   precision: number,
 *   familyName: string
 * }
 */
export const getSqlForCreateColumn = (
  {
    schemaName,
    tableName,
    columnName,
    dataType,
    pk,
    scale,
    precision,
    familyName
  } : {
    schemaName:string,
    tableName: string,
    columnName: string,
    dataType: string,
    pk: boolean,
    scale: number,
    precision: number,
    familyName: string
  }
): Promise<Response<any>> => {
  return http.post('basic_table/sql/column_create', {
    schemaName,
    tableName,
    columnName,
    dataType,
    pk,
    scale,
    precision,
    familyName
  })
}

/**
 * 新建列(获取SQL)
 * @params
 * {
 *   schemaName:string,
 *   tableName: string,
 *   columnName: string,
 *   dataType: string,
 *   pk: boolean,
 *   scale: number,
 *   precision: number,
 *   familyName: string,
 *   comment: string,
 * }
 */
export const createColumn = (
  {
    schemaName,
    tableName,
    columnName,
    dataType,
    pk,
    scale,
    precision,
    familyName,
    comment,
  } : {
    schemaName:string,
    tableName: string,
    columnName: string,
    dataType: string,
    pk: boolean,
    scale: number,
    precision: number,
    familyName: string,
    comment: string,
  }
): Promise<Response<any>> => {
  return http.post('basic_table/column_create', {
    schemaName,
    tableName,
    columnName,
    dataType,
    pk,
    scale,
    precision,
    familyName,
    comment
  })
}

/**
 * 删除列
 * @param schemaName Schema名称
 * @param tableName 基础表名称
 * @param family 列族
 * @param columnName 列名称
 */
export const deleteColumn = ({ schemaName, tableName, family, columnName } : { schemaName: string, tableName: string, family: string, columnName: string }): Promise<Response<any>> => {
  return http.post('basic_table/column/delete', { schemaName, tableName, family, columnName })
}

/**
 * 名称查重 列
 * @param schemaName Schema名称
 * @param tableName 基础表名称
 * @param columnName 列名称
 */
export const duplicateColumn = ({ schemaName, tableName, columnName } : { schemaName: string, tableName: string, columnName: string }): Promise<Response<any>> => {
  return http.post('basic_table/column/duplicate', { schemaName, tableName, columnName })
}

/**
 * 获取二级索引列表
 * @params
 * {
 *   schemaName: string,
 *   tableName: string,
 *   offset: number,
 *   limit: number
 * }
 */
export const getSecondaryIndexList = (
  {
    schemaName,
    tableName,
    offset,
    limit 
  } : {
    schemaName:string,
    tableName: string,
    offset: number,
    limit: number
  }
): Promise<Response<any>> => {
  return http.post('basic_table/secondary_index/list', { schemaName, tableName, offset, limit })
}

/**
 * 新建二级索引
 * @params
 * {
 *   schemaName: string,
 *   tableName: string,
 *   indexName: string,
 *   attrs: string[],
 *   includesAttrs: string[]
 * }
 */
export const createSecondaryIndex = (
  {
    schemaName,
    tableName,
    indexName,
    attrs,
    includesAttrs 
  } : {
    schemaName: string,
    tableName: string,
    indexName: string,
    attrs: string[],
    includesAttrs: string[]
  }
): Promise<Response<any>> => {
  return http.post('secondary_index/create', { schemaName, tableName, indexName, attrs, includesAttrs })
}

/**
 * 运行二级索引
 * @params
 * {
 *   schemaName: string,
 *   tableName: string,
 *   indexName: string,
 *   attrs: string[],
 *   includesAttrs: string[]
 * }
 */
export const runSecondaryIndex = (
  {
    schemaName,
    tableName,
    indexName,
    attrs,
    includesAttrs 
  } : {
    schemaName: string,
    tableName: string,
    indexName: string,
    attrs: string[],
    includesAttrs: string[]
  }
): Promise<Response<any>> => {
  return http.post('index/run', { schemaName, tableName, indexName, attrs, includesAttrs })
}

/**
 * 删除二级索引
 * @param schemaName Schema名称
 * @param tableName 基础表名称
 * @param indexName 索引名称
 */
export const deleteSecondaryIndex = ({ schemaName, tableName, indexName } : { schemaName: string, tableName: string, indexName: string }): Promise<Response<any>> => {
  return http.post('secondary_index/delete', { schemaName, tableName, indexName })
}

/**
 * 获取关联的查询表列表
 * @param schemaName Schema名称
 * @param tableName 基础表名称
 */
export const getConnectionList = ({ schemaName, tableName } : { schemaName: string, tableName: string }): Promise<Response<any>> => {
  return http.post('basic_table/connect_search_table/list', { schemaName, tableName })
}

/**
 * 基础表 数据查询
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
export const filterDataFromBasic = (
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
  return http.post('basic_table/search/data', { secondaryIndex, returnColumns, limit, searchValue, searchTableName, schemaName, tableName })
}

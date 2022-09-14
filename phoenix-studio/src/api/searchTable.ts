import http, { Response } from 'lava-fe-lib/lib-common/http'

/**
 * 获取查询表列表
 * @params
 * {
 *   schemaName: string,
 *   tableName: string,
 *   offset: number,
 *   limit: number
 * }
 */
 export const getSearchTableList = (
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
  return http.post('search_table/list', { schemaName, tableName, offset, limit })
}


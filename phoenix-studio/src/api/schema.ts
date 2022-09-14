import http, { Response } from 'lava-fe-lib/lib-common/http'

/**
 * 获取schema列表
 */
export const getSchemaList = (): Promise<Response<any>> => {
  return http.post('schema/list')
}

/**
 * 新建schema(获取SQL)
 * @param schemaName Schema名称
 */
export const getSqlForCreateSchema = (schemaName: string): Promise<Response<any>> => {
  return http.post('schema/sql/create', { schemaName })
}

/**
 * 删除schema
 * @param schemaName Schema名称
 */
export const deleteSchema = (schemaName: string): Promise<Response<any>> => {
  return http.delete(`schema/delete/${schemaName}`)
}

/**
 * 名称查重schema
 * @param schemaName Schema名称
 */
 export const duplicateSchema = (schemaName: string): Promise<Response<any>> => {
  return http.post('schema/duplicate', { schemaName })
}

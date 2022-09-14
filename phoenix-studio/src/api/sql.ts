import http, { Response } from 'lava-fe-lib/lib-common/http'

/**
 * 开始执行SQL
 * @param sql 需要执行的sql语句
 */
export const executeSql = (sql: string): Promise<Response<any>> => {
  return http.post('sql/execute', { sql })
}

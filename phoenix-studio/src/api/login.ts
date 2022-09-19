import http, { Response } from 'lava-fe-lib/lib-common/http'
/**
 * 登录
 * @params
 * {
 *   userName: string,
 *   password: string
 * }
 */
export const login = ({ userName, password }: { userName: string, password: string }): Promise<Response<any>> => {
  return http.post('login', { userName, password })
}

/**
 * 登出
 */
export const logout = (): Promise<Response<any>> => {
  return http.get('logout')
}

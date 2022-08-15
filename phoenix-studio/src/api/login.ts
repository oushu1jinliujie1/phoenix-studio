import http, { Response } from 'lava-fe-lib/lib-common/http'
// 登录
export const login = (params: any): Promise<Response<any>> => {
  return http.post('/user/login', params)
}
// 退出
export const logout = (): Promise<Response<any>> => {
  return http.post('/user/logout')
}

// 修改密码
export const resetPassword = (userId: number, data: any): Promise<Response<any>> => {
  return http.put(`/lava/user/password/reset/${userId}`, data)
}

// 找回密码
export const retrievePassword = (data: any): Promise<Response<any>> => {
  return http.put('/user/retrieve-password', data)
}

// 检查验证码
export const process = (data: any): Promise<Response<any>> => {
  return http.post('process', data)
}


// 生成CaptchaId
export const captcha = (): Promise<Response<any>> => {
  return http.get('captcha')
}

// 展示图片验证码
export const captchaImage = (image: string): Promise<Response<any>> => {
  return http.get(`captcha/${image}`, { responseType: 'blob' })
}

// 验证用户名/邮箱/手机号是否已存在
export const isExistUser = (params: any): Promise<Response<any>> => {
  return http.post('/user/exist', params)
}


// 用户注册
export const registerUser = (params: any): Promise<Response<any>> => {
  return http.post('/user', params)
}

// 获取用户密码格式信息
export const toGetInitialPasswordFormat = (): Promise<Response<any>> => {
  return http.get('/lava/open/userPwd')
}


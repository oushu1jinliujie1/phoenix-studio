import http, { Response } from 'lava-fe-lib/lib-common/http'

interface Request {
  appName: string,
  group: string,
  key: string,
}

export const getConfig = (params: Request): Promise<Response<any>> => {
  return http.get(`/lava/config/${params.appName}/${params.group}/${params.key}`)
}

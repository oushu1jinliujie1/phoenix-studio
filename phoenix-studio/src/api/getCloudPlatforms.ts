import http, { Response } from 'lava-fe-lib/lib-common/http'

interface CloudPlatform {
  name: string,
  tag: string,
  orders: number,
  chosen: boolean,
  status: number
}

export const getCloudPlatforms = (): Promise<Response<CloudPlatform[]>> => {
  return http.get('/price/cloud-platform')
}

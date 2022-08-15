import http, { Response } from 'lava-fe-lib/lib-common/http'
interface Region {
  name: string,
  tag: string,
  orders: string,
  chosen: string,
  address: string,
  status: string
}

export const getRegionList = (platform: string): Promise<Response<Region[]>> => {
  return http.get(`/price/cloud-platform/${platform}/region`)
}

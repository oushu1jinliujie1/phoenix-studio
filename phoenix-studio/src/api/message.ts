import http, { Response } from 'lava-fe-lib/lib-common/http'
export interface AllInStation {
  userId: number | string,
  pageNum: number,
  pageSize: number,
  sortField?: number | string | null,
  sortOrder?: number | string | null,
  title?: string,
  createdTimeBegin?: string,
  createdTimeEnd?: string,
  type?: string | null,
  subType?: string | null,
  readFlag?: string | number
}

export interface NeighbouringMessage {
  direction: number,
  messageId: number,
  readFlag: number
}

// 获取当前用户站内信
export function getAllInStation(data?: AllInStation): Promise<Response<any>> {
  return http.get('/message/allInStation', { params: data })
}

// 删除站内信
export function deleteMessage(messageIds: any): Promise<Response<any>> {
  return http.delete('/message', { data: messageIds })
}

// 删除全部信息
export function deleteAllMessage(): Promise<Response<any>> {
  return http.delete('/message/all')
}

// 标记消息已读
export function markMessageRead(params: { message_ids: number[] }): Promise<Response<any>> {
  return http.put('/message/markRead', params)
}

// 标记信息全部已读
export function MessageMardReadAll(): Promise<Response<any>> {
  return http.put('/message/markAllRead')
}

// 获取消息类型

export function getMessageType(): Promise<Response<any>> {
  return http.get('/message/type/all')
}

// 获取全部订阅消息

export function getAllMessageSubscribe(): Promise<Response<any>> {
  return http.get('/message/allSubscribe')
}

// 更改订阅信息
export function changeMessageSubscribe(messageSubscribeList: { update_channels: any[] }): Promise<Response<any>> {
  return http.put('/message/updateSubscribeChannel', messageSubscribeList)
}

// 获取某个用户 || 用户组 的订阅信息
export function getUserOrGroupSubscribe(receiverType: string, receiverId: number): Promise<Response<any>> {
  return http.get(`/message/${receiverType}/${receiverId}/subscribe`)
}

// 修改单个类型订阅接收人/组
export function updateSubscribeReceiver(id: number, userIds?: Array<number>[], userGroupIds?: Array<number>[]): Promise<Response<any>> {
  return http.put('/message/updateSubscribeReceiver', { type_id: id, user_ids: userIds, user_group_ids: userGroupIds })
}

// 根据ID 获取消息信息
export function getMessageDetail(params: { messageId: number, readFlag: number }): Promise<Response<any>> {
  return http.get('/message/content', { params })
}

// 获取上一封 下一封消息
export function getNeighbouringMessage(NeighbouringMessageParams: NeighbouringMessage): Promise<Response<any>> {
  return http.get('/message/neighbouring', { params: NeighbouringMessageParams })
}

// message
export function getMessage(data: any): Promise<Response<any>> {
  return http.post('/message', data)
}

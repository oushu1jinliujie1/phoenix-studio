// 只允许大小写英文和数字
export const CHECK_INSTANCE_NAME_RULE_DESCRIPTION = '请填写正确的50位由字母/数字组成的名称'
export const CLUSTER_NAME_CHECK_REGEXP = /^[a-zA-Z0-9]+([a-zA-Z0-9]+)*$/

export function checkIsInstanceName(name: string) {
  return CLUSTER_NAME_CHECK_REGEXP.test(name)
}

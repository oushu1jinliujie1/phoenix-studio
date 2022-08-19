import { message } from 'ant-design-vue-3'

export const copyToClipboard = (val: string) => {
  const textarea = document.createElement('textarea')
  textarea.value = val
  // textarea.hidden = true
  document.body.appendChild(textarea)
  textarea.select()
  document.execCommand('copy')
  document.body.removeChild(textarea)
  message.success('复制成功')
}
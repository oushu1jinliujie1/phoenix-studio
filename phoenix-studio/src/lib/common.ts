import { Ref } from 'vue'

export class FinishCheckItem {
  ref: { value: any } = { value: null }
  msg = ''

  constructor(ref: { value: any }, msg: string) {
    this.ref = ref
    this.msg = msg
  }
}


export function getIsFinish(refs: FinishCheckItem[]) {
  for (let i = 0; i < refs.length; i++) {
    const ref = refs[i].ref
    const msg = refs[i].msg
    const refValue = ref.value
    if ((!refValue) || (!refValue.isFinishRef)) {
      return { result: false, msg }
    }
  }
  return { result: true, msg: '' }
}

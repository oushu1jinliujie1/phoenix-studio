<template>
  <phone-verify
    v-model:formProp="form.phoneVerify"
    :type="type"
    @validate="handleValidate"
  />
</template>

<script>
import PhoneVerify from '../PhoneVerify.vue'
import { reactive } from 'vue'


export default {
  name: 'form-code',
  components: {
    PhoneVerify
  },
  props: {
    type: {
      type: String,
      required: true,
    }
  },
  setup(props, { emit }) {
    const form = reactive({
      phoneVerify: reactive({
        phone: '',
        code: ''
      })
    })
    /**
     * @handler
     * 验证表单
     * 处理错误显示
     */
    const handleValidate = (item) => {
      // console.log(item, 'item')
      if (/^\d{6}$/.test(item.code)) {
        emit('info', {
          status: false,
          value: item
        })
      }
    }

    return {
      form,
      handleValidate,
    }
  }
}
</script>

<template>
  <div ref="inputSearchRef" class="input-search">
    <icon
      v-if="!active"
      color="primary"
      data-test-id="oushudb-worksheet-execute-result-show-search-btn"
      image
      name="statistics-panel/search"
    />
    <a-input
      v-else
      v-model:value="localVal"
      :allowClear="allowClear"
      :loading="loading"
      :placeholder="placeholder"
      data-test-id="oushudb-worksheet-execute-result-search-input"
      @blur="onBlur"
      @change="onChange"
      @focus="onFocus"
      @keyup.enter="onSearch(localVal)"
    >
      <template #suffix>
        <icon
          color="primary"
          image
          name="statistics-panel/search-black"
          style="cursor:pointer;"
          @click="onSearch(localVal)"
        />
      </template>
    </a-input>
  </div>
</template>

<script lang="ts">
import { defineComponent, onBeforeUnmount, onMounted, reactive, ref, toRefs } from 'vue'
import Icon from '@/smart-ui-vue/helper/Icon.vue'

export default defineComponent({
  name: 'InputSearch',
  components: {
    Icon,
  },
  props: {
    value: { type: String, default: '' },
    loading: { type: Boolean, default: false },
    allowClear: { type: Boolean, default: false },
    placeholder: { type: String, default: '' },
  },
  emits: ['update:value', 'change', 'search'],
  setup(props, { emit }) {
    const state = reactive({
      active: false, // 是否悬浮激活输入框
      focus: false, // 输入框是否聚焦
      localVal: props.value,
    })

    const inputSearchRef = ref()

    // 防抖的 change 事件
    const onChange = (() => {
      let timer: number | undefined = undefined

      return (ev: InputEvent) => {
        emit('change', ev)
        emit('update:value', state.localVal)
        if (timer !== undefined) {
          window.clearTimeout(timer)
        }
        timer = window.setTimeout(() => {
          emit('search', state.localVal, ev)
        }, 500)
      }
    })()

    const onFocus = () => {
      state.focus = true
    }

    const onBlur = () => {
      state.focus = false
      onMouseLeave()
    }

    const onSearch = (value: string, ev: Event) => {
      emit('search', value, ev)
    }

    const onMouseEnter = () => {
      state.active = true
    }

    const onMouseLeave = () => {
      // 有输入，不隐藏
      if (state.localVal.length !== 0) return
      // 无输入，有聚焦，不隐藏
      if (state.focus) return
      // 无输入，无聚焦，隐藏
      state.active = false
    }

    onMounted(() => {
      const container = inputSearchRef.value
      container.addEventListener('mouseenter', onMouseEnter)
      container.addEventListener('mouseleave', onMouseLeave)
    })

    onBeforeUnmount(() => {
      const container = inputSearchRef.value
      container.removeEventListener('mouseenter', onMouseEnter)
      container.removeEventListener('mouseleave', onMouseLeave)
    })

    return {
      ...toRefs(state),
      inputSearchRef,
      onChange,
      onFocus,
      onBlur,
      onSearch,
    }
  },
})
</script>

<style lang="scss" scoped>
.input-search {
  display: inline-block;

  :deep(.#{$ant-prefix}-input-affix-wrapper) {
    border-color: $color-primary-blue;

    &:focus {
      box-shadow: 0 0 0 2px #336CFF14;
    }

    &:hover {
      box-shadow: 0 0 0 2px #336CFF14;
    }
  }
}
</style>
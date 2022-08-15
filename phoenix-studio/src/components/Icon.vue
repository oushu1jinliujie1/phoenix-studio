<template>
  <svg :image="image" class="icon">
    <use :xlink:href="`#${MODULE_NAME}/${namespace}${namespace ? '/': ''}${name}`"></use>
  </svg>
</template>

<script>
import { inject } from 'vue'

export default {
  name: 'Icon',
  props: {
    // image模式，在这个模式下无法改变图标颜色，用于处理复杂图形的显示bug
    image: {
      type: Boolean,
      default: false
    },
    // svg 名称
    name: String,
    // (可选) 自定义sprite命名空间
    // 防止多个sprite数据源svg名称冲突
    namespace: {
      type: String,
      default: '',
    },
  },
  setup() {
    return {
      MODULE_NAME: inject('$moduleName')
    }
  }
}
</script>

<style lang="scss">
.icon {
  width: 18px;
  height: 18px;
  overflow: hidden;
  vertical-align: -4px;
  transition: all .35s;
  fill: currentColor;
  stroke: currentColor;

  &[image="true"] {
    fill: none;
    stroke: none;
  }
}
</style>

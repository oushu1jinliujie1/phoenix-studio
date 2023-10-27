<template>
  <PhoenixHeader />
  <Worksheet/>
</template>

<script>
import PhoenixHeader from '@/components/PhoenixHeader'
import Worksheet from '@/components/Worksheet'
import { useRoute } from 'vue-router'
import { onMounted } from 'vue'
export default {
  name: 'WorksheetTest',
  components: { PhoenixHeader, Worksheet },
  setup() {
    return {}
  },
  beforeRouteEnter(to, from, next) {
    next()
  }
}
</script>

<style scoped lang="scss">

</style>

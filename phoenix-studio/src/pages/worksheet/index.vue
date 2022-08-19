<template>
  <Header />
  <Worksheet/>
</template>

<script>
import Header from '@/components/Header'
import Worksheet from '@/components/Worksheet'
import { useRoute } from 'vue-router'
import { onMounted } from 'vue'
export default {
  name: 'WorksheetTest',
  components: { Header, Worksheet },
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

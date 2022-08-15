<template>
  <HeaderOuter />
  <router-view />
</template>

<script>
import HeaderOuter from '@/components/HeaderOuter'

export default {
  name: 'PageSettings',
  components: {
    HeaderOuter
  },
}
</script>
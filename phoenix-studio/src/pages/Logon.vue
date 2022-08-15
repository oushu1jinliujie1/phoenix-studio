<template>
  <HeaderOuter />
  <Login />
</template>

<script>
import HeaderOuter from '@/components/HeaderOuter'
import Login from '@/components/Logon'

export default {
  name: 'PageLogon',
  components: {
    HeaderOuter,
    Login,
  },
}
</script>

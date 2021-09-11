<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
import 'bootstrap/dist/js/bootstrap.min'

export default {
  name: 'App',
  created () {
    this.$bus.$on('myDataFetched', myData => {
      this.$rt.init(myData.settings.webSocketServerUrl, myData.user.token)
    })
  }
}
</script>
<style lang="scss">
html, body {
  height: 100%;
}
.field-error {
  .error {
    display: block;
    color: #ff0000;
  }
}
</style>
 
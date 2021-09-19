<template>
  <div id="app" class="h-100">
    <router-view/>
  </div>
</template>

<script>
import 'bootstrap/dist/js/bootstrap.min'
export default {
  name: 'App',
  created () {
    // initialize websocket client
    this.$bus.$on('myDataFetched', myData => {
      this.$webSocketClient.init(myData.settings.webSocketServerUrl, myData.user.token)
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

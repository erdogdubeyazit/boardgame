<template>
  <div class="logo-wrapper">
    <img class="logo" src="/images/logo.png" />
    <div class="tagline">{{ $t("logo.tagLine") }}</div>
    <LanguageSelector />
  </div>
</template>

<script>
import LanguageSelector from './LanguageSelector.vue'
export default {
  name: 'Logo',
  components: {
    LanguageSelector
  }
}
</script>

<style lang="scss" scoped>
.logo-wrapper {
  text-align: center;
  margin-bottom: 40px;
  .tagline {
    color: #666;
  }
  .logo {
    max-width: 300px;
    margin: auto;
  }
}
</style>

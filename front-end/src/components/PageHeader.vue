<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand">
      <img
        src="/images/logo.png"
        class="logo d-inline-block align-top"
        alt="BoardGame"
      />
    </a>
    <button
      class="navbar-toggler"
      type="button"
      data-toggle="collapse"
      data-target="#navbarSupportedContent"
      aria-controls="navbarSupportedContent"
      aria-expanded="false"
      aria-label="Toggle navigation"
    >
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarSupportedContent">
      <ul class="navbar-nav mr-auto">
        <li class="nav-item">
          <router-link class="nav-link" :to="{ name: 'home' }">{{
            $t("header.home")
          }}</router-link>
        </li>
      </ul>
      <LanguageSelector />
      <ul class="navbar-nav my-lg-0">
        <li class="nav-item dropdown">
          <a
            class="nav-link dropdown-toggle"
            href="#"
            id="navbarDropdown"
            role="button"
            data-toggle="dropdown"
            aria-haspopup="true"
            aria-expanded="false"
          >
            {{ $t("header.welcome") }}, {{ user.name }}
          </a>
          <div
            class="dropdown-menu dropdown-menu-right"
            aria-labelledby="navbarDropdown"
          >
            <a class="dropdown-item" @click="signOut" href="#">{{
              $t("header.signOut")
            }}</a>
          </div>
        </li>
      </ul>
    </div>
  </nav>
</template>

<script>
import { mapGetters } from 'vuex'
import userService from '@/services/user'
import notify from '@/utils/notify'
import LanguageSelector from './LanguageSelector.vue'

export default {
  name: 'PageHeader',
  computed: {
    ...mapGetters(['user'])
  },
  components: {
    LanguageSelector
  },
  mounted () {
    if (!this.user.authenticated) {
      this.$store.dispatch('getMyData')
    }
  },
  methods: {
    setLanguage (lang) {
      this.$i18n.locale = lang
    },
    signOut () {
      this.$webSocketClient.logout()

      userService
        .signOut()
        .then(() => {
          this.$store.dispatch('logout')
          this.$router.push({ name: 'login' })
        })
        .catch((error) => {
          notify.error(error.message)
        })
    }
  }
}
</script>
<style scoped>
.logo {
  max-height: 50px;
  max-width: 200px;
}
</style>

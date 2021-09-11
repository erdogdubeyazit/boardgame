<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light sticky-top">
    <router-link class="navbar-brand" :to="{ name: 'home' }"
      >BEYAZIT</router-link
    >
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
            <a class="dropdown-item" @click="signOut()" href="#">{{
              $t("header.signOut")
            }}</a>
          </div>
        </li>
      </ul>
    </div>
  </nav>
</template>

<script>
import 'bootstrap/dist/js/bootstrap.min'
import { mapGetters } from 'vuex'
import meService from '@/services/me'
import notify from '@/utils/notify'

export default {
  name: 'PageHeader',
  computed: {
    ...mapGetters(['user', 'activeGame'])
  },
  mounted () {
    if (!this.user.authenticated) {
      this.$store.dispatch('getMyData')
    }
  },
  methods: {
    signOut () {
      this.$rt.logout()

      meService
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

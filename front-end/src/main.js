import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import axios from 'axios'

import { i18n } from './i18n'

import Vuelidate from 'vuelidate'

import eventBus from './event-bus'
import webSocketClient from '@/websocket-client'

// axios initialization
axios.defaults.baseURL = '/api'
axios.defaults.headers.common.Accept = 'application/json'
axios.interceptors.response.use(
  response => response,
  (error) => {
    return Promise.reject(error)
  }
)

Vue.use(Vuelidate)

Vue.config.productionTip = false

Vue.prototype.$bus = eventBus
Vue.prototype.$webSocketClient = webSocketClient

new Vue({
  router,
  store,
  i18n,
  render: h => h(App)
}).$mount('#app')

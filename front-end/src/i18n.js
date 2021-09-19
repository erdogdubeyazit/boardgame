import Vue from 'vue'
import VueI18n from 'vue-i18n'
import { en, dutch } from './locale'

Vue.use(VueI18n)

export const i18n = new VueI18n({
  locale: 'en',
  messages: {
    en: en,
    dutch: dutch
  }
})

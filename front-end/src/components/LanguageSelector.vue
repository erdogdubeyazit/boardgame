<template>
  <div>
    <div class="btn-group btn-group-toggle" data-toggle="buttons">
      <label
        class="btn btn-outline-secondary btn-sm"
        v-bind:class="{ active: selectedLanguage === 'en' }"
        @click="selectLanguage('en')"
      >
        <input type="radio" name="options" id="english" /> English
      </label>
      <label
        class="btn btn-outline-secondary btn-sm"
        v-bind:class="{ active: selectedLanguage === 'dutch' }"
        @click="selectLanguage('dutch')"
      >
        <input type="radio" name="options" id="dutch" /> Dutch
      </label>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LanguageSelector',
  data () {
    return {
      selectedLanguage: ''
    }
  },
  methods: {
    handleSelectedLanguageChange (selectedLanguage) {
      this.$i18n.locale = selectedLanguage
    },
    selectLanguage (language) {
      this.selectedLanguage = language
    }
  },
  watch: {
    selectedLanguage: {
      handler: function (val, oldVal) {
        this.handleSelectedLanguageChange(val)
      }
    }
  },
  mounted () {
    this.selectedLanguage = this.$i18n.locale
  }
}
</script>

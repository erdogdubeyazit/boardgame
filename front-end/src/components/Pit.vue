<template>
  <div class="pit w-100 h-50 rounded text-center" v-bind:class="{ 'cursor-pointer': playable }" @click="play">
    {{ itemCount }}
  </div>
</template>

<script>
export default {
  name: 'Pit',
  props: ['itemCount', 'pitIndex', 'playable'],
  methods: {
    play () {
      if (this.playable === true) {
        this.$emit('onPlay', this.pitIndex)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.cursor-pointer {
  cursor: pointer;
}
.pit {
  background-color: burlywood;
  padding-top: 50%;
  font-size: 2em;
}
</style>

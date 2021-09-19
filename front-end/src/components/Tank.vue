<template>
    <div class="tank w-100 h-50 rounded text-center font-weight-bold">
        {{ itemCount }}
    </div>
</template>

<script>
export default {
  name: 'Tank',
  props: ['itemCount']
}
</script>
<style lang="scss" scoped>
.tank{
    background-color: lightgray;
    padding-top: 50%;
    font-size: 3em;
}
</style>

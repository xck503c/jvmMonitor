<template>
  <div class="respMsg">
    <json-viewer :value="msg" :expand-depth=5 copyable boxed sort></json-viewer>
  </div>
</template>

<script>
  export default {
    name: "RespLayer",
    data() {
      return {
        msg: ''
      }
    },
    props: {
      layerid: {//自动注入的layerid
        type: String,
        default: ''
      },
      iframeData: {//传递的数据
        type: Object,
        default: () => {
          return {};
        }
      },
      lydata:{
        type: Object,
        default: () => {
          return {};
        }
      }
    },
    methods: {
      quxiao () {
        this.$layer.close(this.layerid);
      }
    },
    watch: {
      iframeData: {
        handler: function () {
          this.msg = this.iframeData;
        },
        deep: true,
        immediate: true
      }
    }
  }
</script>

<style scoped>
  .respMsg {
    margin: 50px auto;
  }
</style>

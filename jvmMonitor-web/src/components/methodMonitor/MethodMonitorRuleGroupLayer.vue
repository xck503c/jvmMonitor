<template>
  <div class="respMsg">
    <form-create v-model="fApi" :rule="rule" :option="options"></form-create>
  </div>
</template>

<script>
  export default {
    name: "ProcessonMonitorRuleLayer",
    data() {
      return {
        fApi: {},
        id: '',
        options: {
          onSubmit: (formData) => {
            formData.id = this.id;
            this.$axios({
              url: '/methodMonitor/rule/group/save',
              method: 'post',
              data: formData,
              headers: {
                'Content-Type': 'application/json'
              }
            }).then(response => {
              alert(JSON.stringify(response.data));
              parent.location.reload(); // 父页面刷新
            }).catch(error => {
              console.log(error);
            });
          }
        },
        rule: [
          {
            type: "group",
            field: "ruleGroup",
            props: {
              rules: [
                {type: "input", field: "name", title: "规则名"},
                {type: "input", field: "className", title: "类名"},
                {type: "input", field: "methodName", title: "方法名"},
                {
                  type: "checkbox",
                  title: "条件",
                  field: "condition",
                  options: [
                    {value: "1", label: "调用前", disabled: false},
                    {value: "2", label: "调用后", disabled: false},
                    {value: "3", label: "查看异常信息", disabled: false}
                  ]
                }
              ]
            }
          }
        ]
      }
    },
    props: {
      layerid: {//自动注入的layerid
        type: String,
        default: ''
      },
      iframeData: {//传递的数据
        type: Array,
        default: () => {
          return [];
        }
      },
      lydata: {
        type: Object,
        default: () => {
          return {};
        }
      }
    },
    methods: {
      quxiao() {
        this.$layer.close(this.layerid);
      }
    },
    watch: {
      iframeData: {
        handler: function () {
          this.id = this.iframeData.id;
          this.rule[0].value = this.iframeData;
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

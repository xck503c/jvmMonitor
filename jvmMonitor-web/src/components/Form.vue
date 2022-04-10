<template>
  <el-row :gutter="40">
    <el-col :span="5">
      <div class="menu">
        <el-menu :unique-open="true" class="el-menu-vertical-demo" style="text-align: left;">
          <el-submenu :index="item.id + ''" v-for="(item,index) in menuList" :key="index">
            <template slot="title">
              <i class="el-icon-menu"></i>
              <span>{{item.menuName}}</span>
            </template>

            <el-menu-item :index="citem.id + ''" v-for="(citem,cindex) in menuList[index].children"
                          @click="query(citem.id)" :key="cindex">
              {{citem.templeteName}}
            </el-menu-item>
          </el-submenu>

        </el-menu>
      </div>
    </el-col>
    <el-col :span="12">
      <form-create v-model="fApi" :rule="rule" :option="options"></form-create>
    </el-col>
  </el-row>
</template>

<script>
  import RespLayer from "./RespLayer";

  export default {
    name: 'Form',
    data() {
      return {
        fApi: {},
        //菜单列表
        menuList: [],
        msg: '', //表单响应数据
        rule: [], //规则
        processonInfo: {
          pid: '',
          processonName: ''
        },
        uri: '', //表单请求uri
        options: {
          // 显示重置表单按扭
          resetBtn: true,
          // 表单提交按扭事件
          onSubmit: formData => {
            formData.pid = this.processonInfo.pid;
            this.$axios({
              url: this.uri,
              method: 'post',
              data: formData,
              headers: {
                'Content-Type': 'application/json'
              }
            }).then(response => {
              // this.msg = response.data;
              this.$layer.iframe({
                content: {
                  content: RespLayer,
                  parent: this,
                  data: {iframeData: response.data}
                },
                area: ['900px', '600px'],
                title: '响应',
                maxmin: true,
                shade: false,
                shadeClose: false,
                cancel: () => { //关闭弹窗事件
                  alert('关闭?');
                }
              });
            }).catch(error => {
              console.log(error);
            });
          }
        }
      };
    },
    created() {
      // 将数据放在当前组件的数据内
      this.processonInfo.pid = this.$route.query.pid;
      this.processonInfo.processonName = this.$route.query.processonName;
      this.$axios({
        url: '/ft/menuList',
        method: 'get',
      }).then(response => {
        this.menuList = response.data;
      }).catch(error => {
        alert(error);
      });
    },
    methods: {
      query(id) {
        this.$axios({
          url: '/ft/templete',
          method: 'get',
          params: {
            templeteId: id
          }
        }).then(response => {
          this.uri = response.data.uri;
          var templeteStr = JSON.stringify(response.data.rule, function(key, value) {
            if (typeof value == 'function') {
              return `func ${value}`;
            }

            return value;
          });
          var templete = JSON.parse(templeteStr, function (key, value) {
            if (typeof value == 'string') {
              return value.indexOf('func') > -1 ? new Function(`return ${value.replace('func', '')}`)() : value;
            }
            return value;
          });
          this.rule.pop();
          this.rule.push(templete);
        }).catch(error => {
          alert(error);
        });
      },
      chargeCountQuery() {
        this.rule.pop();
        this.rule.push(
          {
            type: "el-col",
            children: [{
              type: "el-row",
              props: {gutter: 9},
              children: [{
                type: "input",
                props: {readonly: true},
                field: "className",
                value: "com.hskj.model.cache.visit.UserDiversionTempleteCache",
                title: "类名",
                validate: [{trigger: "blur", required: true, message: "类名不能为空"}]
              }, {
                type: "el-col",
                props: {span: 15},
                children: [{
                  type: "input",
                  props: {readonly: true},
                  field: "method",
                  value: "checkIsMatchTemplet",
                  title: "方法名",
                  validate: [{trigger: "blur", required: true, message: "方法名不能为空"}]
                }, {
                  type: "object",
                  field: "a",
                  props: {
                    rule: [{
                      type: "input",
                      field: "user_id",
                      title: "账户id",
                      validate: [{trigger: "blur", required: true, message: "账户id不能为空"}]
                    }]
                  }
                }, {
                  type: "object",
                  field: "b",
                  props: {
                    rule: [{
                      type: "input",
                      field: "content",
                      title: "待匹配内容",
                      validate: [{trigger: "blur", required: true, message: "待匹配内容不能为空"}]
                    }]
                  }
                },
                  {
                    type: "object",
                    field: "c",
                    props: {
                      rule: [{
                        type: "input",
                        field: "td_code",
                        title: "通道代码",
                        validate: [{trigger: "blur", required: true, message: "通道代码不能为空"}]
                      }]
                    }
                  }
                ]
              }]
            }]
          }
        );
      }
    }
  }
</script>

<style scoped>
  .el-form {
    margin: 50px auto;
    padding: 50px;
    border: 1px solid gray;
    border-radius: 8px;
  }

  .menu {
    margin: 20px auto;
    padding: 20px;
  }
</style>

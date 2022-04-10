<template>
  <el-row>
    <el-table :data="tableData">
      <el-table-column prop="id" label="规则组ID" width="180"/>
      <el-table-column prop="name" label="规则组名称" width="360"/>
      <el-table-column label="操作" width="360">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleEditClick(scope.row.id)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-row>
</template>

<script>
  import MethodMonitorRuleGroupLayer from "./MethodMonitorRuleGroupLayer";

  export default {
    name: "MethodMonitorRule.vue",
    data() {
      return {
        tableData: []
      };
    },
    created() {
      this.$axios({
        url: '/methodMonitor/rule/list',
        method: 'post',
        headers: {
          'Content-Type': 'application/json'
        }
      }).then(response => {
        this.tableData = response.data;
      }).catch(error => {
        alert(error);
      });
    },
    methods: {
      handleEditClick(id) {
        this.$axios({
          url: "/methodMonitor/rule/group/list",
          method: 'post',
          data: {
            "id": id
          },
          headers: {
            'Content-Type': 'application/json'
          }
        }).then(response => {
          response.data.id = id;
          this.$layer.iframe({
            content: {
              content: MethodMonitorRuleGroupLayer,
              parent: this,
              data: {iframeData: response.data}
            },
            area: ['900px', '600px'],
            title: '规则列表',
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
  }
</script>

<style scoped>

</style>

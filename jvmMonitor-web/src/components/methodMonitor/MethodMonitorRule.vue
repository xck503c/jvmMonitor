<template>
  <el-col>
    <el-row>
      <el-table :data="ruleGroupTableData">
        <el-table-column prop="id" label="规则组ID" width="180"/>
        <el-table-column prop="name" label="规则组名称" width="360"/>
        <el-table-column label="操作" width="360">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="handleEditClick(scope.row.id)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-row>
    <el-row style="margin-top: 50px">
      <el-form>
        <el-col :span="5">
          <el-form-item>
            <el-select v-model="selectedProcesson" placeholder="待注册程序">
              <el-option
                v-for="item in registerProcessonList"
                :key="item.pid"
                :label="item.name"
                :value="item">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item>
            <el-select v-model="selectedRuleGroup" placeholder="待注册模板">
              <el-option
                v-for="item in ruleGroupList"
                :key="item.id"
                :label="item.name"
                :value="item">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="1">
          <el-form-item>
            <template slot-scope="scope">
              <el-button type="primary" size="mini" @click="handleEditClick(scope.row.id)">添加</el-button>
            </template>
          </el-form-item>
        </el-col>
      </el-form>
    </el-row>
    <el-row>
      <el-table :data="registerRuleGroupTableData">
        <el-table-column prop="pid" label="PID" width="90"/>
        <el-table-column prop="processonName" label="程序名" width="180"/>
        <el-table-column prop="id" label="规则组ID" width="180"/>
        <el-table-column prop="name" label="规则组名称" width="180"/>
        <el-table-column label="操作" width="90">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="handleEditClick(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-row>
  </el-col>
</template>

<script>
  import MethodMonitorRuleGroupLayer from "./MethodMonitorRuleGroupLayer";

  export default {
    name: "MethodMonitorRule.vue",
    data() {
      return {
        selectedProcesson: {},
        selectedRuleGroup: {},
        registerProcessonList: [], //注册程序的下拉框
        ruleGroupList: [], //规则组的下拉框
        ruleGroupTableData: [],  //规则组的表格数据
        registerRuleGroupTableData: [] //规则组的程序注册数据
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
        var response = response.data;
        if (response.code == 200) {
          this.ruleGroupTableData = response.data;
        } else {
          alert(response.msg);
        }
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
          var response = response.data;
          if (response.code == 200) {
            var data = response.data;
            data.id = id;
            this.$layer.iframe({
              content: {
                content: MethodMonitorRuleGroupLayer,
                parent: this,
                data: {iframeData: data}
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
          } else {
            alert(response.msg);
          }
        }).catch(error => {
          console.log(error);
        });
      }
    }
  }
</script>

<style scoped>

</style>

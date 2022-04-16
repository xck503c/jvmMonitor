<template>
  <el-row :gutter="20">
    <el-table :data="tableData">
      <el-table-column prop="pid" label="PID" width="180"/>
      <el-table-column prop="processonName" label="程序名" width="360"/>
      <el-table-column prop="status" label="状态" width="180"/>
      <el-table-column label="操作" width="360">
        <template slot-scope="scope">
          <el-button type="primary" @click="handleRegisterClick(scope.row.pid)" size="mini">注册</el-button>
          <el-button type="info" size="mini" @click="handleQueryClick(scope.row)">查询</el-button>
          <el-button type="danger" size="mini" @click="handleRuleListClick(scope.row.pid)">监控规则</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-row>
</template>

<script>
  export default {
    name: 'TableData',
    data() {
      return {
        msg: '',
        tableData: []
      };
    },
    //页面数据初始化
    created() {
      // var response = JSON.parse("{\"resp\":[{\"pid\":124,\"processonName\":\"LongSmsSpliceMain\",\"status\":\"未注册\"}]}");
      // this.tableData = response.resp;
      this.$axios({
        url: '/jp/list',
        method: 'get',
      }).then(response => {
        // var result = JSON.parse(response.data);
        //response.data表示响应数据
        var response = response.data;
        if (response.code == 200) {
          this.tableData = response.data;
        } else {
          alert(response.msg);
        }
      }).catch(error => {
        alert(error);
      });
    },
    methods: {
      handleRegisterClick(pid) {
        this.$axios({
          url: '/jp/register',
          method: 'post',
          data: {
            pid: pid
          },
          transformRequest: [function (data) {
            let ret = '';
            for (let it in data) {
              ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&';
              ret += it + '=' + data[it] + '&';
            }
            return ret;
          }],
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        }).then(response => {
          var response = response.data;
          alert(response.msg);
        }).catch(error => {
          alert(error.data);
        });
      },
      handleQueryClick(row) {
        this.$router.push({
          path: '/Form',
          query: {
            pid: row.pid,
            processonName: row.processonName
          }
        })
      },
      handleRuleListClick(pid) {
        this.$axios({
          url: "/methodMonitor/rule/jp/list",
          method: 'post',
          data: {
            "pid": pid
          },
          headers: {
            'Content-Type': 'application/json'
          }
        }).then(response => {
          var response = response.data;
          if (response.code == 200) {
            this.$router.push(
              {
                path: '/ProcessonMonitorRule',
                query: {
                  pid: pid,
                  data: response.data
                }
              }
            )
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
  .responseMsg {
    margin: 50px auto;
    padding: 50px;
    border: 1px solid gray;
    border-radius: 8px;
  }

  /*.menu {*/
  /*margin: 50px auto;*/
  /*padding: 20px;*/
  /*}*/
</style>

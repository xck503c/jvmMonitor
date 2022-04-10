<template>
  <div class="is-justify-center">
    <el-row>
      <el-form ref="form" label-width="80px" style="border:1px solid #C4E1C5;padding:20px;">
        <el-table :data="tableData">
          <el-table-column prop="id" label="规则组ID" width="180"/>
          <el-table-column prop="name" label="规则组名称" width="360">
            <!-- 规则组下拉框，如果id为空则渲染，所以末尾固定有一个为空的数据 -->
            <template slot-scope="scope">
              <span v-if="scope.row.id !=''">{{scope.row.id}}</span>
              <el-select v-model="selectedRule" v-if="scope.row.id ==''" placeholder="请选择规则组id" @change="ruleChange(scope.row)">
                <el-option
                  v-for="item in ruleList"
                  :key="item.id"
                  :label="item.name"
                  :value="item">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="360">
            <template slot-scope="scope">
              <el-button type="info" size="mini" v-if="scope.row.id ==''">添加</el-button>
              <el-button type="primary" size="mini" v-if="scope.row.id !=''" @click="handleQuery(scope.row.id)">查询</el-button>
              <el-button type="danger" size="mini" v-if="scope.row.id !=''" @click="handleDelete(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
    </el-row>
  </div>
</template>

<script>
  import RespLayer from "../RespLayer";

  export default {
    name: "ProcessonMonitorRule",
    data() {
      return {
        pid: -1,
        tableData: [],
        ruleList: [],
        selectedRule: {}
      }
    },
    created() {
      this.pid = this.$route.query.pid;
      if (this.$route.query.data) {
        this.tableData = this.$route.query.data;
      }
      this.tableData.push({"id":"","name":""});
      this.$axios({
        url: '/methodMonitor/rule/list',
        method: 'post',
        headers: {
          'Content-Type': 'application/json'
        }
      }).then(response => {
        if (Array.isArray(response.data))  {
          var newList = [];
          for (var index in response.data) {
            var item = response.data[index];
            var isExists = false;
            for (var index1 in this.tableData) {
              var tableItem = this.tableData[index1];
              if (tableItem.id == item.id) {
                isExists = true;
                break;
              }
            }
            if (!isExists) {
              newList.push(item);
            }
          }
          this.ruleList = newList;
        } else {
          alert(response.data);
        }
      }).catch(error => {
        alert(error);
      });
    },
    methods: {
      ruleChange(row){
        var ids = [];
        for (var index in this.tableData) {
          if(this.tableData[index].id == '') continue;
          ids.push(this.tableData[index].id);
        }

        ids.push(this.selectedRule.id);

        this.$axios({
          url: '/methodMonitor/rule/update',
          method: 'post',
          data: {
            pid: this.pid,
            ids: ids
          },
          headers: {
            'Content-Type': 'application/json'
          }
        }).then(response => {
          if (Array.isArray(response.data))  {
            this.tableData = response.data;
            this.tableData.push({"id":"","name":""});
          } else {
            alert(response.data);
          }
        }).catch(error => {
          alert(error);
        });
      },
      handleQuery(id) {
        this.$axios({
          url: '/methodMonitor/rule/result/query',
          method: 'post',
          data: {
            id: id,
            size: 100,
          },
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
          alert(error);
        });
      },
      handleDelete(id) {
        var ids = [];
        for (var index in this.tableData) {
          if(this.tableData[index].id == '' || this.tableData[index].id == id) continue;
          ids.push(this.tableData[index].id);
        }

        this.$axios({
          url: 'http://localhost:8080/methodMonitor/rule/update',
          method: 'post',
          data: {
            pid: this.pid,
            ids: ids
          },
          headers: {
            'Content-Type': 'application/json'
          }
        }).then(response => {
          if (Array.isArray(response.data))  {
            this.tableData = response.data;
            this.tableData.push({"id":"","name":""});
          } else {
            alert(response.data);
          }
        }).catch(error => {
          alert(error);
        });
      }
    }
  }
</script>

<style scoped>
  .respMsg {
    margin: 50px auto;
  }
</style>

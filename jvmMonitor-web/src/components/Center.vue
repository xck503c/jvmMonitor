<template>
  <el-row :gutter="20">
    <el-col :span="6">
      <div class="menu">
        <el-menu class="el-menu-vertical-demo" :default-active="this.$router.path" router style="text-align: left;">
          <el-menu-item index="/Register">
            <i class="el-icon-menu"></i>
            <span slot="title">程序列表</span>
          </el-menu-item>
          <el-menu-item index="/MethodMonitorRule">
            <i class="el-icon-menu"></i>
            <span slot="title">方法监控规则管理</span>
          </el-menu-item>
        </el-menu>
      </div>
    </el-col>
    <el-col :span="15">
      <router-view></router-view>
    </el-col>
  </el-row>
</template>

<script>
  export default {
    name: 'Center'
  }
</script>

<style scoped>
  .menu {
    margin: 50px auto;
    padding: 20px;
  }
</style>

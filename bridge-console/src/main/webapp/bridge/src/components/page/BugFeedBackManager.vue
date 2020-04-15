<template>
  <section>
    <el-row class="errPage-container" style="margin-top: 10px">
      <el-col :span="10">
        <a class="text-jumbo" href="https://gitee.com/Jay_git/bridge/issues/new?issue%5Bassignee_id%5D=0&issue%5Bmilestone_id%5D=0">点击跳转到 Issues >></a>
        <div>
          <h3 style="font-size: 14px;color: #909399">感谢您对配置中心的支持！</h3>
        </div>
        <h3 style="font-size: 14px;color: #909399">您可以在反馈页面提交bug或者新的想法！</h3>
      </el-col>

      <el-col :span="14">
        <img src="../../../src/svg/404.svg" width="530" height="460">
      </el-col>
    </el-row>
  </section>

</template>

<script>
  import eventBus from '../../common/js/eventBus.js'


  export default {
    data() {
      return {}
    },
    mounted() {
      eventBus.$emit('firstColumn','其他');
      eventBus.$emit('secondColumn','bug反馈');
      eventBus.$emit('toPage','/bug_feedBack');
    }
  }


</script>

<style rel="stylesheet" scoped>
  .text-jumbo {
    font-size: 20px;
    font-weight: 600;
    font-family: PingFang SC;
  }

  .errPage-container {
    padding-left: 200px;
    padding-top: 100px;
    background-color: #eef3f6;
  }
</style>

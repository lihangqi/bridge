<template>

  <section>
    <el-row class="errPage-container" style="margin-top: 10px">
      <el-col :span="10">
        <a class="text-jumbo" href="https://gitee.com/Jay_git/bridge/blob/master/README.md">点击跳转到开发文档 >></a>
        <div>
          <h3 style="font-size: 14px;color: #909399">感谢您对配置中心的支持！</h3>
        </div>
        <h3 style="font-size: 14px;color: #909399">您可以在开发文档页跳转后查看相关文档！</h3>

      </el-col>

      <el-col :span="14">
        <img src="../../../src/svg/500.svg" width="530" height="460">
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
      eventBus.$emit('firstColumn', '其他');
      eventBus.$emit('secondColumn', '开发文档');
      eventBus.$emit('toPage', '/devDoc_manager');
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

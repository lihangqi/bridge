<template>

  <section>
    <el-row class="errPage-container" style="margin-top: 10px">
      <el-col :span="10">
        <h1 class="text-jumbo">403</h1>
        <h3 style="font-size: 20px;color: #909399">抱歉，您无权访问该页面</h3>
        <el-button size="small" type="primary" icon="el-icon-arrow-left" style="margin-top: 20px" @click="back">
          返回
        </el-button>
      </el-col>
      <el-col :span="14">
        <img src="../../../src/svg/403.svg" width="530" height="460">
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
    methods: {
      back() {
        if (this.$route.query.noGoBack) {
          this.$router.push({
            path: '/dashboard'
          })
        } else {
          this.$router.go(-1)
        }
      },
    },
    mounted() {
      eventBus.$emit('firstColumn', '未知地');
      eventBus.$emit('secondColumn', '403');
      eventBus.$emit('toPage', '/403');
    }
  }
</script>

<style rel="stylesheet" scoped>
  .text-jumbo {
    font-size: 120px;
    font-weight: 600;
    color: #484848;
    font-family: PingFang SC;
  }

  .errPage-container {
    padding-left: 200px;
    padding-top: 100px;
    background-color: #eef3f6;
  }
</style>

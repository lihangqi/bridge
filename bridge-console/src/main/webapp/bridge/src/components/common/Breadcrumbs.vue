<template>

  <section>
    <el-row class="el-breadcrumb-my">
      <div>
        <Breadcrumb>
          <BreadcrumbItem>
            <Icon type="ios-apps"/>
            {{firstColumn}}
          </BreadcrumbItem>
          <BreadcrumbItem :to="toPage">
            {{secondColumn}}
          </BreadcrumbItem>
        </Breadcrumb>
      </div>
    </el-row>
  </section>
</template>

<script>
  import eventBus from '../../common/js/eventBus.js'

  export default {
    name: "breadcrumbs",
    data() {
      return {
        firstColumn: '',
        secondColumn: '',
        toPage: '',
      }
    },
    methods: {},
    mounted() {
      let that = this;
      eventBus.$on('firstColumn', function (firstColumn) {
        that.firstColumn = firstColumn;
      });
      eventBus.$on('secondColumn', function (secondColumn) {
        that.secondColumn = secondColumn;
      });
      eventBus.$on('toPage', function (toPage) {
        that.toPage = toPage;
      });
    }
  }
</script>

<style scoped>

</style>

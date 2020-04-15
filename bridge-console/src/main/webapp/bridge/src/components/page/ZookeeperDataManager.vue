<template>
  <section>
    <!--menu-->
    <el-row style="margin-top: 15px;background-color: #fff">
      <el-menu :default-active="activeIndex" mode="horizontal" @select="doWhenSelectChanged"
               style="width: 100%">
        <el-submenu index="0">
          <template slot="title">
            <span style="font-weight: bold;font-size: 18px;">{{menuName}} /</span>
            <span v-if="envId === '0'" style="font-weight: bold;font-size: 26px">{{env}}</span>
            <span v-if="envId === '1'" style="font-weight: bold;font-size: 26px">{{env}}</span>
            <span v-if="envId === '2'" style="font-weight: bold;font-size: 26px;color: #E6A23C">{{env}}</span>
            <span v-if="envId === '3'" style="font-weight: bold;font-size: 26px;color: #F56C6C">{{env}}</span>
          </template>
          <div v-for="(team,index) in selectorList" :key="index">
            <el-submenu :index="team.teamId + ''" v-if="team.configAppList.length > 0">
              <template slot="title">{{team.teamName}}</template>
              <el-menu-item v-if="team.configAppList.length > 0"
                            v-for="(app,appIndex) in team.configAppList"
                            :index="team.teamId + '-' + app.appId" :key="appIndex">
                {{app.appName}}
              </el-menu-item>
            </el-submenu>
            <el-menu-item disabled :index="team.teamId + ''" v-else>{{team.teamName}}</el-menu-item>
          </div>
        </el-submenu>
      </el-menu>
    </el-row>

    <!--    <el-row style="padding-top: 15px;padding-left: 15px; text-align: center; background-color: #fff">-->
    <!--      <el-col>-->
    <!--        <span style="font-size: 20px; font-family: PingFang SC">{{teamName}} / {{appName}}</span>-->
    <!--      </el-col>-->
    <!--    </el-row>-->

    <el-row>
      <div style="background: #fff;padding-left: 20px;padding-top: 20px">
        <el-button size="small" type="primary"
                   icon="el-icon-refresh"
                   @click="consistencyDbToZk">同步数据库配置项数据到zookeeper
        </el-button>

        <el-button size="small" type="primary" @click="searchTree">刷新</el-button>
      </div>
    </el-row>

    <el-row class="el-table-my" style="padding-top: 20px">
      <Alert show-icon style="font-size: 13px">
        列表数据为zookeeper节点数据。同步数据库配置项数据到zookeeper时会以数据库数据为准，将zookeeper上数据同步后会全量下发到实例。
      </Alert>
    </el-row>

    <el-row class="el-table-my">
      <el-col class="my-style-table">
        <el-table :data="zkTreeList" fit show-header stripe style="width: 100%;margin-top: 10px;margin-bottom: 20px"
                  header-row-class-name="table-header" v-loading="listLoading">
          <el-table-column type="expand" class="my-table-expand">
            <template slot-scope="scope">
              <div v-for="item in scope.row.machineNodeDataList" style="padding-left: 20px">
                <el-form>

                  <el-col>
                    <el-col :span="5">
                      <el-form-item label="实例: " style="margin-bottom: -5px">
                        <el-tag size="mini" type="success">{{item.machineHost}}</el-tag>
                      </el-form-item>
                    </el-col>

                    <el-col :span="2">
                      <el-form-item style="margin-bottom: -22px">
                        <Divider type="vertical"/>
                      </el-form-item>
                    </el-col>

                    <el-col :span="5">
                      <el-form-item label="配置项状态: " style="margin-bottom: -5px">
                        <el-tag size="mini" v-if="item.needUpdate === 0" type="primary">不需要通知实例更新</el-tag>
                        <el-tag size="mini" v-if="item.needUpdate === 1" type="warning">需要通知实例更新</el-tag>
                        <el-tag size="mini" v-if="item.needUpdate === 2" type="warning">实例未使用</el-tag>
                      </el-form-item>
                    </el-col>


                    <el-col :span="2">
                      <el-form-item style="margin-left: 40px;margin-bottom: -5px">
                        <Divider type="vertical"/>
                      </el-form-item>
                    </el-col>


                    <el-col :span="8">
                      <el-form-item label="配置项版本号 :" style="margin-bottom: -5px">
                        <el-tag size="mini" type="info">{{item.version}}</el-tag>
                      </el-form-item>
                    </el-col>
                  </el-col>

                </el-form>
              </div>
              <div v-if="scope.row.machineNodeDataList.length === 0">
                <el-form label-position="left" inline style="margin-bottom: -22px;font-size: 10px;margin-left: 15px">
                  <el-form-item>
                    <el-tag size="mini">无实例数据</el-tag>
                  </el-form-item>
                </el-form>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="配置项" prop="key">
            <template slot-scope="scope">
<!--              <div style="border:rgb(217,216,221) 2px; background-color: rgb(241,241,243);-->
<!--              border-radius: 4px;font-weight: bold">-->
                <div style="border:rgb(217,216,221) 2px; ">
                  <span style="border:rgb(217,216,221) 2px; background-color: rgb(241,241,243);
              border-radius: 4px;font-weight: bold;padding: 8px 8px 8px 8px;">{{scope.row.key}}</span></div>
<!--              </div>-->
            </template>
          </el-table-column>
          <el-table-column label="生效值" prop="value">
            <template slot-scope="scope">
              <el-tooltip v-if="scope.row.value" :content="scope.row.value" placement="top"
                          effect="dark">
                <div style="font-weight: bold">
                      <span v-if="scope.row.value.length <= 50" style="padding: 5px;">
                        {{scope.row.value}}</span>
                  <span v-if="scope.row.value.length > 50">
                        {{scope.row.value && scope.row.value.substr(0,50)}}
                       <span style="cursor: pointer;">...</span>
                      </span>
                </div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="版本号">
            <template slot-scope="scope" v-if="scope.row.version">
              <el-tag size="mini" type="info">{{scope.row.version}}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>

  </section>
</template>

<script>
  import eventBus from '../../common/js/eventBus.js'

  let baseUrL = '/bridge';

  export default {
    data() {
      return {
        activeIndex: '0',
        env: '',
        envId: '',
        teamName: '',
        appName: '',
        menuName: '点击选择系统',
        zkTreeList: [],
        selectorList: {},
        listLoading: true,
        isPageOpen: false,
      };
    },

    methods: {

      // 同步db数据到zk
      consistencyDbToZk() {
        let env = localStorage.getItem('env');
        let teamName = this.teamName;
        let appName = this.appName;
        if (!teamName || !appName) {
          this.$message({showClose: true, message: "请先选择需要同步系统", type: 'warning'});
          return;
        }
        let des = '【' + teamName + '-' + appName + '-' + env + '】';
        this.$confirm('您确认同步' + des + '的数据吗?', '提示', {type: 'warning'}).then(() => {
          NProgress.start();
          let params = {};
          params.envId = localStorage.getItem('envId');
          params.appId = this.appId;
          let url = baseUrL + '/consistencyDbToZk';
          this.$http.get(url, {params: params}).then(function (res) {
            if (res.body.success) {
              this.$message({showClose: true, message: "操作成功", type: 'success'});
            } else {
              this.$message({showClose: true, message: res.body.message, type: 'error'});
            }
            // 查询列表数据
            this.searchTree();
          }).catch(err => {
            console.log('error', err);
          });
          NProgress.done();
        })
      },

      // table数据
      doWhenSelectChanged(tab, event) {
        if (event.length === 3) {
          this.teamId = event[2].split('-')[0];
          this.appId = event[2].split('-')[1];
          localStorage.setItem('health_teamId', this.teamId);
          localStorage.setItem('health_appId', this.appId);
          // 查询标签
          this.searchTag();
          // 查询列表数据
          this.searchTree();
        }
      },
      // 查询标签数据
      searchTag() {
        NProgress.start();
        let params = {};
        params.appId = this.appId;
        let url = baseUrL + '/queryTeamNameAndAppNameByAppId';
        this.$http.get(url, {params: params}).then(function (res) {
          this.teamName = res.body.result.teamName;
          this.appName = res.body.result.appName;
          this.menuName = this.teamName + ' / ' + this.appName;
          localStorage.setItem('health_teamName', this.teamName);
          localStorage.setItem('health_appName', this.appName);
          localStorage.setItem('health_menuName', this.menuName);
        }).catch(err => {
          console.log('error', err);
        });
        NProgress.done();
      },

      // 查询列表数据
      searchTree() {
        this.listLoading = true;
        NProgress.start();
        let params = {};
        params.envId = localStorage.getItem('envId');
        params.appId = this.appId;
        if (!this.appId || !params.envId) {
          this.listLoading = false;
          this.$message({showClose: true, message: "请先选择系统", type: 'warning'});
          return;
        }
        let url = baseUrL + '/queryZkDataList';
        this.$http.get(url, {params: params}).then(function (res) {
          this.zkTreeList = res.body.result;
          this.listLoading = false;
        }).catch(err => {
          console.log('error', err);
        });
        NProgress.done();
      },
      // 查询下拉框筛选列表
      getSelectorData() {
        let params = {};
        let url = baseUrL + '/getSelectorData';
        this.$http.get(url, {params: params}).then(function (res) {
          this.selectorList = res.body.result;
        }).catch(err => {
          console.log('error', err);
        });
      },

    },
    mounted() {
      this.isPageOpen = true;
      this.getSelectorData();
      eventBus.$emit('firstColumn', '监控');
      eventBus.$emit('secondColumn', 'zookeeper数据');
      eventBus.$emit('toPage', '/health_analysis');
      this.teamId = localStorage.getItem('health_teamId');
      this.appId = localStorage.getItem('health_appId');
      this.teamName = localStorage.getItem('health_teamName');
      this.appName = localStorage.getItem('health_appName');
      this.menuName = localStorage.getItem('health_menuName') ? localStorage.getItem('health_menuName') : '点击选择系统';
      this.env = localStorage.getItem('env');
      this.envId = localStorage.getItem('envId');
      this.activeIndex = this.teamId + '-' + this.appId;
      if (this.appId) {
        this.searchTree();
      } else {
        this.listLoading = false;
      }
      eventBus.$on('change', () => {
        this.env = localStorage.getItem('env');
        this.envId = localStorage.getItem('envId');
        if (this.appId && this.isPageOpen) {
          this.searchTree();
        } else {
          this.listLoading = false;
        }
      })
    },
    destroyed() {
      this.isPageOpen = false;
    }
  };
</script>

<style scoped>
  .my-table-expand {
    font-size: 5px;
  }

  .my-table-expand label {
    width: 80px;
    color: #99a9bf;
  }

  /*.my-style-table .el-table td, .my-style-table .el-table th.is-leaf {*/
  /*border-bottom: none;*/
  /*}*/
  .my-style-table .el-table::before {
    left: 0;
    bottom: 0;
    width: 100%;
    height: 0px;
  }
</style>

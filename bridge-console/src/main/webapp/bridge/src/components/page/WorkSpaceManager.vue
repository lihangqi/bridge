<template>
  <section>
    <el-row class="el-operate-my">
      <el-col :span="18" class="toolbar">
        <el-col>
          <div style="padding-top: 10px; position: relative">
            <div style="width: 72px;height: 72px;position: absolute;left :0;top:0;">
              <Avatar style="width: 72px;height: 72px;" size="large"
                      src="http://acxnn.oss-cn-hangzhou.aliyuncs.com/bridge/operate_pic.png"/>
            </div>
            <div>
              <div class="welcome">你好，{{realName}}，祝你开心每一天！</div>
              <div class="user_info_des">
                <Icon type="ios-briefcase"/>
                {{userTeamName}}
                <Divider type="vertical"/>
                <Icon type="ios-ribbon"/>
                {{accountRoleStr}}
                <Divider type="vertical"/>
                <Icon type="md-person"/>
                {{realName}}
              </div>

            </div>
          </div>
        </el-col>
      </el-col>

      <el-col :span="6">
        <el-row class="el-operate-my" style="padding-top: 20px">
          <el-col :span="6">
            <el-row class="project_des">项目数</el-row>
            <el-row class="project_number">{{projectNumber}}</el-row>
          </el-col>
          <el-col :span="2">
            <el-row>
              <Divider class="line" type="vertical"/>
            </el-row>
          </el-col>
          <el-col :span="6">
            <el-col class="project_des">发布次数</el-col>
            <el-col class="project_number">{{pushCount}}</el-col>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <el-row>
      <a-card class="box-card" title="我的项目" :bordered=false>
        <a href="#/system_manager" slot="extra">查看所有</a>
        <div v-if="!appDefVoList || appDefVoList.length === 0">
          <div style="color: #999;font-size: 14px;text-align: center">暂无数据</div>
        </div>

        <a-card-grid class="a-card-grid-item" v-for="(item,index) in appDefVoList" :key="index">
          <div class="page-price-recommend">
            <span>{{item.teamName  && item.teamName.substr(0,5)}}</span>
            <span v-if="item.teamName &&　item.teamName.length > 5">..</span>
          </div>
          <el-col :span="24">
            <el-row>
              <span class="item-icon">
                  {{item.appName && item.appName.length > 0 ? item.appName.charAt(0).toUpperCase(): '#'}}
              </span>
              <span style="font-size: 14px;color: #000000; padding-left: 5px;font-weight: 500">
                <span>{{item.appName  && item.appName.substr(0,10)}}</span>
                <span v-if="item.appName &&　item.appName.length > 16" style="cursor: pointer;color: #4a90e2;">...</span>
              </span>
            </el-row>
          </el-col>
          <el-col>
            <div class="app_info">
              <span>{{item.appDes  && item.appDes.substr(0,16)}}</span>
              <span v-if="item.appDes &&　item.appDes.length > 16" style="cursor: pointer;color: #4a90e2;">...</span>
            </div>
          </el-col>

          <div style="padding-left: 55px;padding-top: 10px;overflow:hidden;margin-bottom: -5px">
            <div style="float:left">
              <Icon type="md-hammer" style="color: #515a6e"/>
              <span style="color: #515a6e;font-size: 12px;">
                {{item.gmtCreate}}
              </span>
            </div>
            <div style="color: #515a6e;text-align: right;padding-right: 10px;font-size: 12px;margin-top: -2px">
              <el-button type="text" size="small" @click="toConfigManage(item)">前往</el-button>
            </div>
          </div>
        </a-card-grid>
      </a-card>
    </el-row>


    <el-row>
      <a-card class="box-card" title="最近动态" :bordered=false>
        <a @click="seeAll" slot="extra">查看所有</a>
        <div v-if="!operateLogList || operateLogList.length === 0">
          <div style="color: #999;font-size: 14px;text-align: center">暂无数据</div>
        </div>
        <div v-for="(item,index) in operateLogList" :key="index" class="text item">
          <el-row v-if="index <= 4">
            <div style="width: 42px;height: 42px;position: absolute;left :0;top:0;">
              <Avatar style="width: 42px;height: 42px;" size="large"
                      src="http://acxnn.oss-cn-hangzhou.aliyuncs.com/bridge/head_console.png"/>
            </div>
            <div style="padding-left: 60px">
              <a style="color: #20a0ff">{{item.operateName}}</a>
              <a style="color: #999">在</a>
              <a style="color: #20a0ff">{{item.appName}}</a>
              <a style="color: #999">项目中</a>
              <a style="color: #20a0ff">{{item.operateTypeStr}}</a>
              <a style="color: #999">配置项</a>
              <el-tag size="mini">{{item.configKey}}</el-tag>
              <a v-if="item.operateType === 0" style="color: #999">，当前的版本号为</a>
              <a v-if="item.operateType === 1 || item.operateType === 2" style="color: #999">，被删除时的版本号为</a>
              <el-tag v-if="item.operateType === 0" type="info" size="mini">{{item.versionAfter}}</el-tag>
              <el-tag v-if="item.operateType === 1 || item.operateType === 2" type="success" size="mini">
                {{item.versionBefore}}
              </el-tag>
            </div>
            <div style="padding-left: 60px;margin-top:5px;font-size: 12px">
              <span>{{item.gmtCreate}}</span>
            </div>
            <div v-if="index < 4">
              <Divider></Divider>
            </div>
          </el-row>
        </div>

      </a-card>
    </el-row>


  </section>

</template>

<script>
  import eventBus from '../../common/js/eventBus.js'

  let baseUrL = '/bridge';


  export default {
    data() {
      return {
        realName: '',
        userTeamName: '',
        projectNumber: '',
        pushCount: '',
        appDefVoList: [],
        operateLogList: [],
        listLoading: true,
        cardLoading: true,
        isPageOpen: false,
      }
    },
    computed: {
      accountRoleStr() {
        let accountRole = localStorage.getItem('accountRole');
        if (accountRole === '0') {
          return '系统管理员';
        }
        if (accountRole === '1') {
          return '普通用户';
        }
        if (accountRole === '2') {
          return '团队管理员';
        }
        return '';
      }
    },
    methods: {

      // 查看所有
      seeAll() {
        this.$router.push('/operateLog_manager')
      },

      // 查询工作台数据
      queryWorkSpaceInfo() {
        NProgress.start();
        let url = baseUrL + '/getWorkSpaceInfo';
        let params = {};
        params.envId = localStorage.getItem('envId');
        this.$http.get(url, {params: params}).then(function (res) {
          this.projectNumber = res.body.result.projectNumber;
          this.pushCount = res.body.result.pushCount;
          this.appDefVoList = res.body.result.appDefVoList;
          this.cardLoading = false;
        }).catch(err => {
          console.log('error', err);
        });
        NProgress.done();
      },

      // 查询动态
      queryLatestWork() {
        this.listLoading = true;
        NProgress.start();
        let url = baseUrL + '/queryOperateLogList';
        let params = {};
        params.envId = localStorage.getItem('envId');
        this.$http.get(url, {params: params}).then(function (res) {
          this.operateLogList = res.body.result;
          this.listLoading = false;
        }).catch(err => {
          console.log('error', err);
        });
        NProgress.done();
      },

      // 跳转到发布的页面
      toConfigManage(item) {
        localStorage.setItem('teamId', item.teamId);
        localStorage.setItem('appId', item.id);
        localStorage.setItem('appName', item.appName);
        localStorage.setItem('teamName', item.teamName);
        this.$router.push('/config_manager');
      },
    },
    mounted() {
      this.isPageOpen = true;
      eventBus.$emit('firstColumn', 'Home');
      eventBus.$emit('secondColumn', '工作台');
      eventBus.$emit('toPage', '/welcome');
      this.realName = localStorage.getItem('realName');
      this.userTeamName = localStorage.getItem('userTeamName');
      this.queryWorkSpaceInfo();
      this.queryLatestWork();
      eventBus.$on('change', () => {
        if (this.isPageOpen) {
          this.queryLatestWork();
        }
      })
    },
    destroyed() {
      this.isPageOpen = false;
    }
  }


</script>


<style scoped>

  .value-item {
    width: 100px;
    display: block;
    overflow: hidden;
    word-break: keep-all;
    text-overflow: ellipsis;
  }

  .welcome {
    font-size: 20px;
    font-family: "Helvetica Neue", Helvetica, "microsoft yahei", arial, STHeiTi, sans-serif;
    font-weight: 500;
    line-height: 28px;
    margin-left: 90px;
  }

  .user_info_des {
    font-size: 14px;
    font-family: "Helvetica Neue", Helvetica, "microsoft yahei", arial, STHeiTi, sans-serif;
    line-height: 28px;
    margin-left: 90px;
    margin-top: 10px;
  }

  .project_des {
    text-align: center;
    font-size: 14px;
    font-family: "Helvetica Neue", Helvetica, "microsoft yahei", arial, STHeiTi, sans-serif;
  }

  .project_number {
    font-size: 30px;
    font-family: "Helvetica Neue", Helvetica, "microsoft yahei", arial, STHeiTi, sans-serif;
    font-weight: 500;
    text-align: center;
  }

  .line {
    width: 1px;
    height: 60px;
  }

  .text {
    font-size: 14px;
    color: #999;
  }

  .clearfix:after {
    clear: both
  }

  .box-card {
    width: 100%;
    margin-top: 10px;
    font-size: 16px;
  }

  .a-card-grid-item {
    width: 25%;
    textAlign: 'center';
    padding: 25px 25px 25px 20px;
    overflow: hidden;
    position: relative;
  }

  .app_info {
    font-size: 13px;
    padding-top: 5px;
    padding-right: 10px;
    height: 30px;
    line-height: 25px;
    color: #909399;
    overflow: hidden;
    text-overflow: ellipsis;
    display: block;
    padding-left: 55px;
  }

  .time {
    font-size: 12px;
    color: #999;
  }

  .box-card {
    width: 100%;
    margin-top: 10px;
    font-size: 16px;
  }

  .page-price-recommend {
    width: 110px;
    height: 20px;
    line-height: 20px;
    text-align: center;
    position: absolute;
    top: 16px;
    right: -30px;
    background-color: #19be6b;
    /*background-color: #f60;*/
    color: #fff;
    font-size: 12px;
    transform: rotate(45deg);
  }

  .item-icon {
    background: #303133;
    font-size: 18px;
    border: 1px solid #303133;
    border-radius: 50px;
    width: 32px;
    height: 32px;
    color: #fff;
    font-weight: 500;
    line-height: 30px;
    text-align: center;
    display: inline-block;
    margin-left: 13px;
  }
</style>








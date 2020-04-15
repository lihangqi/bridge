<template>

  <div class="sidebar">
    <Menu theme="dark">
      <MenuGroup title="DashBoard">
        <MenuItem name="5" to="/welcome" v-if="isWelcomeShow">
          <Icon type="md-laptop"/>
          工作台
        </MenuItem>
      </MenuGroup>

      <MenuGroup title="基础信息" v-if="isBasicInfoShow">
        <MenuItem name="10" to="/account_info" v-if="isAccountShow">
          <Icon type="ios-people"/>
          账号管理
        </MenuItem>
        <MenuItem name="20" to="/team_manager" v-if="isTeamManagerShow">
          <Icon type="ios-browsers"/>
          团队管理
        </MenuItem>
        <MenuItem name="30" to="/system_manager" v-if="isSystemManagerShow">
          <Icon type="md-document"/>
          系统管理
        </MenuItem>
      </MenuGroup>
      <MenuGroup title="配置项">
        <MenuItem name="35" to="/config_manager" v-if="isConfigManagerShow">
          <Icon type="logo-buffer"/>
          配置项管理
        </MenuItem>
        <MenuItem name="50" to="/operateLog_manager" v-if="isOperateLogManagerShow">
          <Icon type="md-list"/>
          操作历史
        </MenuItem>
      </MenuGroup>
      <MenuGroup title="监控">
        <MenuItem name="60" to="/health_analysis" v-if="isHealthAnalysisShow">
          <Icon type="md-analytics"/>
          zookeeper
        </MenuItem>
        <MenuItem name="65" to="/system_log" v-if="isSystemLogShow">
          <Icon type="md-cog"/>
          系统日志
        </MenuItem>
      </MenuGroup>
      <MenuGroup title="其他">
        <MenuItem name="70" to="/devDoc_manager" v-if="isDevDocManagerShow">
          <Icon type="ios-book"/>
          开发文档
        </MenuItem>
        <MenuItem name="80" to="/bug_feedBack" v-if="isBugFeedBackShow">
          <Icon type="ios-bug"/>
          bug反馈
        </MenuItem>
      </MenuGroup>
    </Menu>
  </div>


</template>

<script>
  export default {
    data() {
      return {
        isWelcomeShow: false,
        isAccountShow: false,
        isTeamManagerShow: false,
        isSystemManagerShow: false,
        isBasicInfoShow: false,
        isConfigManagerShow: false,
        isOperateLogManagerShow: false,
        isHealthAnalysisShow: false,
        isSystemLogShow:false,
        isDevDocManagerShow: false,
        isBugFeedBackShow: false,
        permissionList: [],
      };
    },
    computed: {},
    methods: {},
    mounted() {
      this.permissionList = localStorage.getItem('permissionList');
      this.isAccountShow = this.permissionList.indexOf("/account_info") >= 0;
      this.isWelcomeShow = this.permissionList.indexOf("/welcome") >= 0;
      this.isTeamManagerShow = this.permissionList.indexOf("/team_manager") >= 0;
      this.isSystemManagerShow = this.permissionList.indexOf("/system_manager") >= 0;
      this.isBasicInfoShow = this.isAccountShow || this.isSystemManagerShow || this.isTeamManagerShow;
      this.isConfigManagerShow = this.permissionList.indexOf("/config_manager") >= 0;
      this.isOperateLogManagerShow = this.permissionList.indexOf("/operateLog_manager") >= 0;
      this.isHealthAnalysisShow = this.permissionList.indexOf("/health_analysis") >= 0;
      this.isSystemLogShow = this.permissionList.indexOf("/health_analysis") >= 0;
      this.isDevDocManagerShow = this.permissionList.indexOf("/devDoc_manager") >= 0;
      this.isBugFeedBackShow = this.permissionList.indexOf("/bug_feedBack") >= 0;
    }

  }
</script>


<style>
  .sidebar {
    display: block;
    position: absolute;
    width: 200px !important;
    left: 0;
    top: 64px;
    bottom: 0;
    z-index: 1;
    background: rgb(22, 23, 26);
    border-bottom: 1px solid #101117;
    box-shadow: 0 0 6px  rgba(0,0,0,.8);
    overflow: auto;
    transition: width 1s;
    /*-webkit-font-smoothing: antialiased;*/
    -moz-transition: width 1s; /* Firefox 4 */
    -webkit-transition: width 1s; /* Safari 和 Chrome */
    -o-transition: width 1s; /* Opera */
  }

  .sidebar a {
    text-decoration: none !important;
  }

  .ivu-menu-dark {
    background: rgb(22, 23, 26);
    width: 200px !important;
  }

  .ivu-menu-dark.ivu-menu-vertical .ivu-menu-item-active:not(.ivu-menu-submenu), .ivu-menu-dark.ivu-menu-vertical
  .ivu-menu-submenu-title-active:not(.ivu-menu-submenu) {
    color: rgb(255, 255, 255);
  }
</style>



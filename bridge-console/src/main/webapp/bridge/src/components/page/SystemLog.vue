<template>
  <section>
    <!--menu-->
    <el-row class="el-operate-my">
      <!--工具条-->
      <el-col :span="24" class="toolbar">
        <el-form :inline="true">
          <el-form-item>
            <el-select v-model="filters.appCode" placeholder="请选择系统" :disabled="isInput" size="small" class="h30">
              <el-option-group v-for="group in selectorList" :key="group.teamName" :label="group.teamName">
                <el-option v-for="item in group.configAppList" :key="item.appCode" :label="item.appName"
                           :value="item.appCode" style="font-size: 13px"/>
              </el-option-group>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-date-picker
              :disabled="isChoseDate"
              v-model="dateTime"
              type="datetimerange"
              :picker-options="pickerOptions"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              size="small"
              style="margin-top: 5px">
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button size="small" type="primary" icon="el-icon-search" v-on:click="onSearch" :disabled="isSearch">查询
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-button-group>
              <el-button size="small" type="primary" icon="el-icon-arrow-left" @click="allClick">所有日志
                <i class="el-icon-news el-icon--right"></i></el-button>
              <el-button size="small" type="primary" icon="el-icon-printer" @click="nowClick">实时日志
                <i class="el-icon-arrow-right el-icon--right"></i></el-button>
            </el-button-group>
          </el-form-item>
        </el-form>
      </el-col>


      <el-col :span="24" style="padding: 1px 5px 20px 20px;background-color: #fff">
        <el-alert style="font-size: 15px"
                  title="提示"
                  type="success"
                  description="「实时日志」一般用于客户端发布、配置项下发时，可以动态监控配置项初始化的情况；
                      「所有日志」提供搜索功能，便于问题定位以及数据查看。日志记录超过1千万条后建议手动清理历史日志。">
        </el-alert>
      </el-col>

      <el-col :span="24">
        <div class="split">
          <div slot="top" class="split_pane_left" v-show="isShowNow">
            <Divider orientation="left">实时日志</Divider>
            <div>
              <el-table :show-header="false" :data="nowTableData" size="mini" height="500px" style="width: 100%;">
                <el-table-column label="系统名称" min-width="10%">
                  <template slot-scope="scope">
                    <div style="color:#303133">{{scope.row.appName}}</div>
                  </template>
                </el-table-column>
                <el-table-column prop="logRecordTime" label="时间" min-width="15%"/>
                <el-table-column prop="logLevelStr" label="日志级别" min-width="5%">
                  <template slot-scope="scope">
                    <div v-if="scope.row.logLevel === 0" style="color:#909399">DEBUG</div>
                    <div v-if="scope.row.logLevel === 1" style="color:#909399">INFO</div>
                    <div v-if="scope.row.logLevel === 2" style="color:#E6A23C">WARN</div>
                    <div v-if="scope.row.logLevel === 3" style="color:#F56C6C">ERROR</div>
                  </template>
                </el-table-column>
                <el-table-column prop="ip" label="服务地址" min-width="10%"/>
                <el-table-column prop="logContent" label="日志内容" min-width="55%"/>
              </el-table>
            </div>
          </div>

          <div slot="top" class="split_pane_left" v-show="isShowAll">
            <Divider orientation="left">所有日志</Divider>
            <div>
              <el-table :show-header="false" :data="allTableData" size="mini" height="500px" style="width: 100%;">
                <el-table-column label="系统名称" min-width="10%">
                  <template slot-scope="scope">
                    <div style="color:#303133">{{scope.row.appName}}</div>
                  </template>
                </el-table-column>
                <el-table-column prop="logRecordTime" label="时间" min-width="15%"/>
                <el-table-column prop="logLevelStr" label="日志级别" min-width="5%">
                  <template slot-scope="scope">
                    <div v-if="scope.row.logLevel === 0" style="color:#909399">DEBUG</div>
                    <div v-if="scope.row.logLevel === 1" style="color:#909399">INFO</div>
                    <div v-if="scope.row.logLevel === 2" style="color:#E6A23C">WARN</div>
                    <div v-if="scope.row.logLevel === 3" style="color:#F56C6C">ERROR</div>
                  </template>
                </el-table-column>
                <el-table-column prop="ip" label="服务地址" min-width="10%"/>
                <el-table-column prop="logContent" label="日志内容" min-width="55%"/>
              </el-table>
              <!--分页工具条-->
              <el-col :span="24" class="toolbar" style="margin-top: 5px">
                <el-pagination layout="total, sizes, prev, pager, next, jumper" style="float:right;"
                               @size-change="configKeyOnSizeChange"
                               @current-change="configKeyOnPageChange"
                               :page-sizes="[5,10,20,50,100]"
                               :current-page.sync="page"
                               :page-count="totalPage"
                               :page-size="size"
                               :total="total">
                </el-pagination>
              </el-col>
            </div>
          </div>
        </div>
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
        isSearch: false,
        isInput: false,
        isChoseDate: false,
        total: 0,
        totalPage: 0,
        size: 50,
        page: 1,
        isShowNow: false,
        isShowAll: true,
        tableLoading: false,
        filters: {
          appCode: '',
          startTime: '',
          endTime: '',
        },
        appCodeList: '',
        appCodeListForWebSocket: '',
        pickerOptions: {
          shortcuts: [
            {
              text: '最近一天',
              onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24);
                picker.$emit('pick', [start, end]);
              }
            }, {
              text: '最近一周',
              onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                picker.$emit('pick', [start, end]);
              }
            }, {
              text: '最近一个月',
              onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                picker.$emit('pick', [start, end]);
              }
            }, {
              text: '最近三个月',
              onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                picker.$emit('pick', [start, end]);
              }
            }]
        },
        dateTime: '',
        split: 0.5,
        listLoading: true,
        isPageOpen: false,
        nowTableData: [],
        allTableData: [],
        selectorList: {},
      };
    },

    methods: {
      initWebSocket: function () {
        let url;
        // 这里是为了本地测试
        if (window.location.hostname === '127.0.0.1' || window.location.hostname === 'localhost') {
          url = 'ws://127.0.0.1:8080/bridge/websocket/';
          // 如果是ip的话就拼接上端口号
        } else if (window.location.host.match("(\\d*\\.){3}\\d*")) {
          // 这里需要后和端服务端口号统一
          url = 'ws://' + window.location.hostname + ":8080" + baseUrL + '/websocket/';
          // 如果是域名则不拼接端口号
        } else if (window.location.host.match("[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?")) {
          url = 'ws://' + window.location.hostname + baseUrL + '/websocket/';
        }
        this.websock = new WebSocket(url + localStorage.getItem('envId') + "/" + this.appCodeListForWebSocket);
        this.websock.onopen = this.open;
        this.websock.onerror = this.error;
        this.websock.onmessage = this.message;
        this.websock.onclose = this.close;
      },

      open: function () {
        console.log("websocket连接成功");
      },

      error: function (e) {
        this.appCodeListForWebSocket = '';
        console.log("websocket连接发生错误");
      },

      message: function (e) {
        if (this.nowTableData == null) {
          this.nowTableData = JSON.parse(e.data);
        } else {
          this.nowTableData.push(JSON.parse(e.data));
        }
      },

      close: function (e) {
        this.appCodeListForWebSocket = '';
        console.log("connection closed");
      },

      // 查询下拉框筛选列表
      getInitData() {
        let params = {};
        let url = baseUrL + '/getSelectorData';
        this.$http.get(url, {params: params}).then(function (res) {
          this.selectorList = res.body.result;
          let data = res.body.result;
          // 取出appCodeList
          for (let i = 0; i < data.length; i++) {
            if (data[i]) {
              for (let j = 0; j < data[i].configAppList.length; j++) {
                this.appCodeList += this.appCodeList ? '&appCodeList=' + data[i].configAppList[j].appCode : 'appCodeList=' + data[i].configAppList[j].appCode;
                this.appCodeListForWebSocket = data[i].configAppList[j].appCode + "," + this.appCodeListForWebSocket;
              }
            }
          }
          this.searchTab();
          this.initWebSocket();
        }).catch(err => {
          console.log('error', err);
        });
      },

      // 查询列表数据
      searchTab() {
        this.tableLoading = true;
        NProgress.start();
        let params = {};
        params.envId = localStorage.getItem('envId');
        params.page = this.page;
        params.size = this.size;
        if (this.filters.appCode) {
          this.appCodeList = 'appCodeList=' + this.filters.appCode;
        }
        if (this.dateTime) {
          params.startTime = dayjs(this.dateTime[0]).format("YYYY-MM-DD HH:mm:ss");
        }
        if (this.dateTime) {
          params.endTime = dayjs(this.dateTime[1]).format("YYYY-MM-DD HH:mm:ss");
        }
        let url = baseUrL + '/querySystemLog';
        this.$http.get(url + '?' + this.appCodeList, {params: params}).then(function (res) {
          this.allTableData = res.body.result;
          this.total = res.body.total;
          this.totalPage = res.body.totalPage;
          this.tableLoading = false;
        }).catch(err => {
          console.log('error', err);
        });
        NProgress.done();
      },

      // 查询
      onSearch() {
        this.searchTab();
      },

      allClick() {
        this.isShowAll = true;
        this.isShowNow = false;
        this.isInput = false;
        this.isChoseDate = false;
        this.isSearch = false;
        this.searchTab();
      },

      nowClick() {
        this.isShowAll = false;
        this.isShowNow = true;
        this.isInput = true;
        this.isChoseDate = true;
        this.isSearch = true;
      },

      // 分页
      configKeyOnPageChange(val) {
        this.page = val;
        this.searchTab();
      },
      // 分页
      configKeyOnSizeChange(val) {
        this.size = val;
        this.searchTab();
      },


    }
    ,
    mounted() {
      eventBus.$emit('firstColumn', '监控');
      eventBus.$emit('secondColumn', '系统日志');
      eventBus.$emit('toPage', '/system_log');
      this.isPageOpen = true;
      this.getInitData();

      eventBus.$on('change', () => {
        if (this.isPageOpen) {
          this.close();
          this.getInitData();
        }
      })
    }
    ,
    destroyed() {
      this.isPageOpen = false;
      // 销毁监听
      this.close();
    }
  }
  ;
</script>

<style scoped>
  .split {
    height: 600px;
    padding: 10px;
    margin-top: -20px;
    /*border: 1px solid #dcdee2;*/
    /*background: #dcdee2;*/
  }

  .split_pane_left {
    padding: 5px;
  }

  .split_pane_right {
    margin-left: 20px;
    padding: 5px;
  }


</style>

<style>
  .h30 .el-input__inner {
    height: 32px !important;
  }
</style>

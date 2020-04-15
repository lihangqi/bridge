<template>

  <section>
    <el-row class="el-operate-my">
      <!--操作栏-->
      <el-col :span="24" class="toolbar">
        <el-form :inline="true">
          <el-form-item>
            <Input v-model="filters.appName" placeholder="按系统名称搜索" style="width:200px;"/>
          </el-form-item>
          <el-form-item>
            <Input v-model="filters.configKey" placeholder="按配置项搜索" style="width:200px;"/>
          </el-form-item>
          <el-form-item>
            <el-button size="small" type="primary" icon="el-icon-search" v-on:click="onSearch">查询</el-button>
          </el-form-item>
        </el-form>
      </el-col>

      <el-col class="el-table-my" style="margin-top: -10px;margin-bottom: 10px">
        <el-alert style="font-size: 15px"
                  title="注意"
                  type="warning"
                  description="回滚操作会将【操作前的值】与【操作前的版本号】直接【全量下发】，请谨慎操作！">
        </el-alert>
      </el-col>

      <el-col :span="24" class="el-table-my">
        <!--列表-->
        <el-table :data="operateHistoryList" stripe fit v-loading="listLoading" style="width: 100%;" size="medium">
          <!--<el-table-column type="index" label="#"/>-->
          <el-table-column prop="appName" label="系统名称"/>
          <el-table-column prop="operateName" label="操作人"/>
          <el-table-column prop="gmtCreate" label="操作时间" width="200px"/>
          <el-table-column prop="configKey" label="配置项"/>
          <el-table-column label="操作前的值">
            <template slot-scope="scope">
              <span>{{scope.row.valueBefore && scope.row.valueBefore.substr(0,10)}}</span>
              <el-tooltip v-if="scope.row.valueBefore &&　scope.row.valueBefore.length > 10"
                          :content="scope.row.valueBefore" placement="top" effect="dark">
                <span style="cursor: pointer;color: #4a90e2;">...</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="操作前的版本号" width="150px">
            <template slot-scope="scope">
              <el-tag type="info" size="mini">{{scope.row.versionBefore}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="valueAfter" label="操作后的值">
            <template slot-scope="scope">
              <span>{{scope.row.valueAfter && scope.row.valueAfter.substr(0,10)}}</span>
              <el-tooltip v-if="scope.row.valueAfter &&　scope.row.valueAfter.length > 10"
                          :content="scope.row.valueAfter" placement="top" effect="dark">
                <span style="cursor: pointer;color: #4a90e2;">...</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="操作后的版本号" width="150px">
            <template slot-scope="scope" v-if="scope.row.versionAfter">
              <el-tag size="mini" type="info">{{scope.row.versionAfter}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作类型" width="120px">
            <template slot-scope="scope">
              <el-tag size="mini" v-if="scope.row.operateType === 0">{{scope.row.operateTypeStr}}</el-tag>
              <el-tag size="mini" type="info" v-if="scope.row.operateType === 1">{{scope.row.operateTypeStr}}</el-tag>
              <el-tag size="mini" type="warning" v-if="scope.row.operateType === 3">{{scope.row.operateTypeStr}}
              </el-tag>
              <el-tag size="mini" type="danger" v-if="scope.row.operateType === 2">{{scope.row.operateTypeStr}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="medium" v-if="scope.row.valueBefore" @click="rollBack(scope.row)">回滚
              </el-button>
              <el-button type="text" style="color: #999" size="medium" v-if="!scope.row.valueBefore"
                         @click="rollBack(scope.row)">N/A
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
      <!--工具条-->
      <el-col :span="24" class="el-page-my">
        <el-pagination layout="total,  prev, pager, next, jumper" @current-change="handleCurrentChange"
                       :page-count="totalPage" :size="size" :total="total" style="float:right;">
        </el-pagination>
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
        filters: {
          appName: '',
          configKey: '',
        },
        listData: [],
        total: 0,
        totalPage: 0,
        size: 10,
        page: 1,
        listLoading: true,
        operateHistoryList: [],
        isPageOpen: false,
      }
    },
    methods: {

      // 回滚
      rollBack(row) {
        let url = baseUrL + '/rollbackData';
        NProgress.start();
        this.$confirm('确认回滚该记录吗?', '提示', {type: 'warning'}).then(() => {
          this.$http.post(url, {id: row.id}, {emulateJSON: true}).then(function (res) {
            if (res.body.success) {
              this.teamAddOrEditFormVisible = false;
              this.$message({showClose: true, message: "操作成功", type: 'success'});
              this.onSearch();
            } else {
              this.$message({showClose: true, message: res.body.message, type: 'error'});
            }
          }).catch((err) => {
            console.log('error', err);
            this.$message({showClose: true, message: err, type: 'error'});
          })
        })


        NProgress.done();
      },

      // 查询
      onSearch() {
        this.listLoading = true;
        let params = {};
        if (this.filters.appName) {
          params.appName = this.filters.appName;
        }
        if (this.filters.configKey) {
          params.configKey = this.filters.configKey;
        }
        params.page = this.page;
        params.size = this.size;
        params.envId = localStorage.getItem('envId');
        this.queryLatestWork(params ? params : '')
      },

      // 分页
      handleCurrentChange(val) {
        this.page = val;
        this.onSearch();
      },

      // 查询动态
      queryLatestWork(params) {
        NProgress.start();
        let url = baseUrL + '/queryOperateLogList';
        this.$http.get(url, {params: params}).then(function (res) {
          this.operateHistoryList = res.body.result;
          this.total = res.body.total;
          this.totalPage = res.body.totalPage;
          this.listLoading = false;
        }).catch(err => {
          console.log('error', err);
        });
        NProgress.done();
      },
    },
    mounted() {
      this.isPageOpen = true;
      eventBus.$emit('firstColumn', '监控');
      eventBus.$emit('secondColumn', '操作日志');
      eventBus.$emit('toPage', '/operateLog_manager');
      this.onSearch();
      eventBus.$on('change', () => {
        if (this.isPageOpen) {
          this.onSearch();
        }
      })
    },
    destroyed() {
      this.isPageOpen = false;
    }
  }


</script>


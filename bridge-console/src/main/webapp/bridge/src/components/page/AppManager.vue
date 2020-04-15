<template>
  <section>
    <el-row class="el-operate-my">
      <!--工具条-->
      <el-col :span="24" class="toolbar">
        <el-form :inline="true">
          <el-form-item>
            <Input v-model="appName" placeholder="按系统名称搜索" style="width: 200px" clearable/>
          </el-form-item>
          <el-form-item>
            <el-button size="small" type="primary" icon="el-icon-search" v-on:click="appHandleSearch">查询
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-button size="small" type="primary" icon="el-icon-plus" v-on:click="appHandleAdd">新增</el-button>
          </el-form-item>
        </el-form>
      </el-col>

      <el-col :span="24" class="el-table-my">
        <!--列表-->
        <el-table :data="appDataList" stripe fit v-loading="listLoading" style="width: 100%;" size="medium">
          <el-table-column type="index" label="#"/>
          <el-table-column prop="appName" label="系统名称">
            <template slot-scope="scope">
              <span>{{scope.row.appName && scope.row.appName.substr(0,10)}}</span>
              <el-tooltip v-if="scope.row.appName &&　scope.row.appName.length > 10"
                          :content="scope.row.appName" placement="top" effect="dark" class="item">
                <span style="cursor: pointer;color: #4a90e2;">...</span>
              </el-tooltip>
            </template>
          </el-table-column>

          <el-table-column prop="appCode" label="系统编码"/>
          <el-table-column prop="ownerRealName" label="系统负责人">
            <template slot-scope="scope">
              <span>{{scope.row.ownerRealName && scope.row.ownerRealName.substr(0,10)}}</span>
              <el-tooltip v-if="scope.row.ownerRealName &&　scope.row.ownerRealName.length > 10"
                          :content="scope.row.ownerRealName" placement="top" effect="dark" class="item">
                <span style="cursor: pointer;color: #4a90e2;">...</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="teamName" label="所属团队"/>
          <el-table-column label="系统描述">
            <template slot-scope="scope">
              <span>{{scope.row.appDes && scope.row.appDes.substr(0,20)}}</span>
              <el-tooltip v-if="scope.row.appDes &&　scope.row.appDes.length > 20"
                          :content="scope.row.appDes" placement="top" effect="dark" class="item">
                <span style="cursor: pointer;color: #4a90e2;">...</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button type="text" size="medium" @click="appHandleEdit(scope.row)">编辑</el-button>
              <Divider type="vertical"/>
              <el-button type="text" size="medium" @click="deleteApp(scope.row)">删除</el-button>
              <Divider type="vertical"/>
              <el-button type="text" size="medium" @click="toConfigManage(scope.row)">前往</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
      <!--分页工具条-->
      <el-col :span="24" class="el-page-my">
        <el-pagination layout="total,  prev, pager, next, jumper" @current-change="appHandleCurrentChange"
                       :page-count="totalPage" :size="size" :total="total" style="float:right;">
        </el-pagination>
      </el-col>

      <!--弹框-->
      <el-dialog :title="appAddOrEditStatus.title" :close-on-click-modal="false"
                 width="32%" size="tiny" :visible.sync="addOrEditFormVisible">
        <el-form :model="appAddOrEditForm" :rules="addOrEditRules" ref="appAddOrEditForm" label-width="100px">
          <el-form-item label="系统名称" prop="appName">
            <el-input size="small" v-model="appAddOrEditForm.appName" auto-complete="off" placeholder="请填写系统名称"/>
          </el-form-item>

          <el-form-item label="系统编码" prop="appCode">
            <el-input size="small" v-model="appAddOrEditForm.appCode" auto-complete="off" :disabled="true"
                      placeholder="由系统自动生成"/>
          </el-form-item>

          <el-form-item label="所属团队" prop="teamId">
            <el-select size="small" v-model="appAddOrEditForm.teamId" placeholder="请选择团队" clearable
                       @change="doWhenTeamIdChanged">
              <el-option
                v-for="(item,index) in teamList"
                :label="item.value"
                :value="item.key"
                :key="index"
                :disabled="item.key === 1">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="系统负责人" prop="ownerId">
            <el-select size="small" v-model="appAddOrEditForm.appOwner" placeholder="请选择负责人" clearable>
              <el-option
                v-for="(item,index) in accountList"
                :label="item.value"
                :value="item.key"
                :key="index">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="系统描述" prop="appDes">
            <el-input size="small" v-model="appAddOrEditForm.appDes" auto-complete="off" placeholder="请填写系统相关描述"/>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button size="mini" @click="cancel">取 消</el-button>
          <el-button size="mini" type="primary" @click="appAddOrEdit">确 定</el-button>
        </div>
      </el-dialog>
    </el-row>

  </section>
</template>

<script>
  import eventBus from '../../common/js/eventBus.js'

  let baseUrL = '/bridge';

  export default {
    data() {
      return {
        appName: '',
        accountList: [],
        teamList: [],
        appDataList: [],
        total: 0,
        totalPage: 0,
        size: 10,
        page: 1,
        test: true,
        listLoading: true,
        // 新增或编辑
        addOrEditFormVisible: false,
        appAddOrEditForm: {},
        appAddOrEditStatus: {
          title: '新增',
          value: 0,
          rules: '',
        },
        permissionsFormVisible: false,
        addOrEditRules: {
          appName: [
            {required: true, message: '请输入客户端名称', trigger: 'blur'}
          ],
          teamId: [
            {required: true, message: '请选择团队', trigger: 'blur'}
          ],
          appOwner: [
            {required: true, message: '请选择负责人', trigger: 'blur'}
          ],
          appDes: [
            {required: true, message: '请选择系统描述', trigger: 'blur'}
          ],
        },
      }
    },
    computed: {},
    methods: {

      // 跳转到发布的页面
      toConfigManage(item) {
        localStorage.setItem('teamId', item.teamId);
        localStorage.setItem('appId', item.id);
        localStorage.setItem('appName', item.appName);
        localStorage.setItem('teamName', item.teamName);
        this.$router.push('/config_manager');
      },

      // 显示新增界面
      appHandleAdd: function () {
        this.appAddOrEditStatus = {
          title: '新增',
          value: 0
        };
        this.addOrEditFormVisible = true;
        // 显示新增页面的默认值
        this.appAddOrEditForm = {
          teamName: '',
          appName: '',
          appOwner: '',
          appCode: '',
          teamId: '',
          appDes: '',
          ownerRealName: '',
          id: '',
        };
      },

      // 显示编辑界面
      appHandleEdit: function (row) {
        this.appAddOrEditStatus = {
          title: '编辑',
          value: 1
        };
        this.addOrEditFormVisible = true;
        this.doRequestWhenEdit(row.teamId);
        // 显示编辑页面的默认值
        this.appAddOrEditForm = {
          teamName: row.teamName,
          appName: row.appName,
          appCode: row.appCode,
          appOwner: row.appOwner,
          teamId: row.teamId,
          appDes: row.appDes,
          ownerRealName: row.ownerRealName,
          id: row.id,
        };
      },

      // 取消
      cancel() {
        this.addOrEditFormVisible = false;
        this.$refs['appAddOrEditForm'].clearValidate();
      },

      // 当选中的team发生改变时，重新拉取team下的人员
      doWhenTeamIdChanged(val) {
        if (!val){
          console.log('-->',val);
          return;
        }
        let url = baseUrL + '/queryAccountByTeamId';
        let params = {};
        params.teamId = val;
        this.$http.get(url, {params: params}).then(function (res) {
          this.accountList = res.body.result;
          this.appAddOrEditForm.appOwner = '';
        }).catch(err => {
          console.log('error', err);
        });
      },

      // 当打开编辑框的时候，根据teamId查询出对应的accountList
      doRequestWhenEdit(val) {
        let url = baseUrL + '/queryAccountByTeamId';
        let params = {};
        params.teamId = val;
        this.$http.get(url, {params: params}).then(function (res) {
          this.accountList = res.body.result;
        }).catch(err => {
          console.log('error', err);
        });
      },

      /**
       * 获取账号、团队列表
       *
       * @param val
       */
      getAccountAndTeamList() {
        NProgress.start();
        let params = {};
        let url = baseUrL + '/queryTeamList';
        this.$http.get(url, {params: params}).then(function (res) {
          this.teamList = res.body.result;
          this.listLoading = false;
        }).catch(err => {
          console.log('error', err);
        });
        NProgress.done();
      },

      /**
       * 新增或编辑
       */
      appAddOrEdit() {
        this.$refs['appAddOrEditForm'].validate((valid) => {
          if (valid) {
            let url;
            if (this.appAddOrEditStatus.value === 0) {
              url = baseUrL + '/addApp';
            } else if (this.appAddOrEditStatus.value === 1) {
              url = baseUrL + '/editApp';
            }
            this.$http.post(url, this.appAddOrEditForm).then(function (res) {
              if (res.body.success) {
                this.addOrEditFormVisible = false;
                this.$message({showClose: true, message: "操作成功", type: 'success'});
                this.appHandleSearch();
              } else {
                this.$message({showClose: true, message: res.body.message, type: 'error'});
                this.addOrEditFormVisible = true
              }
            }).catch(err => {
              this.$message({showClose: true, message: '表单校验出错', type: 'warning'});
              return false;
            });

          } else {
            //表单校验失败
            this.$message({showClose: true, message: "表单校验失败", type: 'error'});
            return false;
          }
        });
      },

      // 分页
      appHandleCurrentChange(val) {
        this.page = val;
        this.appHandleSearch();
      },

      // 获取列表
      appHandleSearch() {
        this.listLoading = true;
        this.queryAppPageList()
      },

      // 查询列表
      queryAppPageList() {
        NProgress.start();
        let url = baseUrL + '/queryAppPageList';
        let params = {};
        params.page = this.page;
        params.size = this.size;
        params.appName = this.appName;
        this.$http.get(url, {params: params}).then(function (res) {
          this.appDataList = res.data.result;
          this.total = res.body.total;
          this.totalPage = res.body.totalPage;
          this.listLoading = false;
        }).catch(err => {
          console.log('error', err)
        });
        NProgress.done();
      },

      // 删除
      deleteApp(n) {
        this.$confirm('确认删除该记录吗?', '提示', {type: 'warning'}).then(() => {
          let url = baseUrL + '/deleteApp';
          this.$http.post(url, {id: n.id}, {emulateJSON: true})
            .then(function (res) {
              if (res.body.success) {
                this.addOrEditFormVisible = false;
                this.$message({showClose: true, message: "操作数据成功", type: 'success'});
                this.appHandleSearch();
              } else {
                this.$message({showClose: true, message: res.body.message, type: 'error'});
              }
            }).catch(err => {
            console.log('error', err)
          });
        }).catch((err) => {
          this.$message({showClose: true, message: err, type: 'error'});
        });
      },
    },

    mounted() {
      eventBus.$emit('firstColumn', '基础信息');
      eventBus.$emit('secondColumn', '系统管理');
      eventBus.$emit('toPage', '/system_manager');
      this.appHandleSearch();
      this.getAccountAndTeamList();
    },

    watch: {
      // 监听输入框
      appName(val) {
          // 查询列表数据
          this.queryAppPageList();
      }
    }
  }


</script>

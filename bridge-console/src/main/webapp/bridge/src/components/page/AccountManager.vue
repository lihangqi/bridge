<template>
  <section>
    <el-row class="el-operate-my">
      <!--操作栏-->
      <el-col :span="24" class="toolbar">
        <el-form :inline="true">
          <el-form-item>
            <Input v-model="accountName" placeholder="按账号搜索" style="width:200px;" clearable/>
          </el-form-item>
          <el-form-item>
            <el-button size="small" type="primary" icon="el-icon-search" v-on:click="handleSearch">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button size="small" type="primary" icon="el-icon-plus" v-on:click="handleAdd">新增</el-button>
          </el-form-item>
        </el-form>
      </el-col>

      <el-col class="el-table-my" style="margin-top: -20px;margin-bottom: 20px">
        <Alert show-icon style="font-size: 13px" closable>
          变更账号的所属团队时，要确保该账号不是某个系统的系统负责人，如果是则需要先更换该系统的系统负责人
        </Alert>
      </el-col>

      <el-col :span="24" class="el-table-my">
        <!--列表-->
        <el-table :data="listData" stripe fit v-loading="listLoading" style="width: 100%;" size="medium">
          <el-table-column type="index" label="#"/>
          <el-table-column prop="accountName" label="账号"/>
          <el-table-column label="角色">
            <template slot-scope="scope">
              <el-tag size="mini" v-if="scope.row.accountRole === 0">{{scope.row.accountRoleStr}}</el-tag>
              <el-tag size="mini" type="info" v-if="scope.row.accountRole === 1">{{scope.row.accountRoleStr}}</el-tag>
              <el-tag size="mini" type="success" v-if="scope.row.accountRole === 2">{{scope.row.accountRoleStr}}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="teamName" label="所属团队"/>
          <el-table-column prop="realName" label="姓名"/>
          <el-table-column prop="accountMobile" label="手机号"/>
          <el-table-column prop="email" label="邮箱"/>
          <el-table-column label="操作" width="200">
            <template slot-scope="scope">
              <div v-if="scope.row.accountRole !== 0">
                <el-button type="text" size="medium" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                <Divider type="vertical"/>
                <el-button type="text" size="medium" @click="changeEnabledState(scope.$index, scope.row)">
                  <span v-if="scope.row.enabledState === 0" class="notEnabled">禁用</span>
                  <span v-if="scope.row.enabledState === 1">启用</span>
                </el-button>
                <Divider type="vertical"/>
                <el-button type="text" size="medium" @click="deleteUser(scope.$index, scope.row)">删除
                </el-button>
              </div>
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

      <!--dialog-->
      <el-dialog :title="addOrEditStatus.title" :visible.sync="addOrEditFormVisible"
                 :close-on-click-modal="false" size="tiny" top="5%" width="32%">
        <el-form :model="addOrEditForm" ref="addOrEditForm" label-width="100px">
          <el-form-item label="账号" prop="accountName">
            <el-input size="small" v-model="addOrEditForm.accountName" :disabled="accountNameEnable"/>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input size="small" v-model="addOrEditForm.password"/>
          </el-form-item>
          <el-form-item label="姓名" prop="realName">
            <el-input size="small" v-model="addOrEditForm.realName"/>
          </el-form-item>
          <el-form-item label="角色" prop="accountRole">
            <el-select size="small" v-model="addOrEditForm.accountRole" placeholder="请选择角色" clearable>
              <el-option
                v-for="(item,index) in optionsAccountRole"
                :label="item.value"
                :value="item.key"
                :key="index"
                :disabled="item.key === 0">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="团队" prop="teamId">
            <el-select size="small" v-model="addOrEditForm.teamId" placeholder="请选择团队" clearable>
              <el-option
                v-for="(item,index) in optionsTeam"
                :label="item.value"
                :value="item.key"
                :key="index">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="手机号码" prop="accountMobile">
            <el-input size="small" v-model="addOrEditForm.accountMobile"/>
          </el-form-item>
          <el-form-item label="邮箱" prop="userEmail">
            <el-input size="small" v-model="addOrEditForm.email"/>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button size="mini" @click="cancel">取 消</el-button>
          <el-button size="mini" type="primary" @click="addOrEdit">确 定</el-button>
        </div>
      </el-dialog>
    </el-row>

  </section>
</template>

<script>

  let baseUrL = '/bridge';
  import eventBus from '../../common/js/eventBus.js'

  export default {
    data() {
      return {
        accountName: '',
        listData: [],
        total: 0,
        totalPage: 0,
        size: 10,
        page: 1,
        listLoading: true,
        optionsAccountRole: [],
        optionsTeam: [],
        accountNameEnable: false,
        addOrEditFormVisible: false,
        addOrEditForm: {},
        addOrEditStatus: {
          title: '新增',
          value: 0,
        },
      }
    },
    computed: {},
    methods: {

      // 获取用户账号列表
      getUserPage(params) {
        NProgress.start();
        let url = baseUrL + '/queryUserPageList';
        this.$http.get(url, {params: params}).then(function (res) {
          this.total = res.body.total;
          this.totalPage = res.body.totalPage;
          this.listData = res.body.result;
          this.listLoading = false;
        }).catch(err => {
          console.log('error', err)
        });
        NProgress.done();
      },

      // 取消
      cancel() {
        this.addOrEditFormVisible = false;
      },

      // 获取角色枚举
      getAccountRoleEnum(val) {
        let url = baseUrL + '/queryEnumByTag';
        let params = {};
        params.tag = val;
        this.$http.get(url, {params: params}).then(function (res) {
          if (val === 1) {
            this.optionsAccountRole = res.data.result;
          }
          if (val === 2) {
            this.optionsTeam = res.data.result;
          }
        }).catch(err => {
          console.log('error', err);
        });
      },


      // 新增或者编辑
      addOrEdit() {
        let url;
        // 参数校验
        if (this.addOrEditForm.accountName.trim().length === 0) {
          this.$message({showClose: true, message: "账号不能为空", type: 'error'});
          return false;
        }
        // 新增
        if (this.addOrEditStatus.value === 0) {
          url = baseUrL + '/addUser';
          if (this.addOrEditForm.accountRole === '') {
            this.$message({showClose: true, message: "角色不能为空", type: 'error'});
            return false;
          }
          if (!this.addOrEditForm.password) {
            this.$message({showClose: true, message: "密码不能为空", type: 'error'});
            return false;
          }
          // 编辑更新
        } else if (this.addOrEditStatus.value === 1) {
          url = baseUrL + '/editUser';
        }
        // 发送http请求
        this.$http.post(url, this.addOrEditForm, {emulateJSON: true}).then(function (res) {
          if (res.body.success) {
            this.addOrEditFormVisible = false;
            this.$message({showClose: true, message: "操作成功", type: 'success'});
            this.handleSearch();
          } else {
            this.$message({showClose: true, message: res.body.message, type: 'error'});
            this.addOrEditFormVisible = true
          }
        })
      },

      //显示编辑界面
      handleEdit: function (index, row) {
        this.addOrEditStatus = {
          title: '编辑',
          value: 1
        };
        this.accountNameEnable = true;
        this.addOrEditFormVisible = true;
        this.addOrEditForm = {
          realName: row.realName,
          accountName: row.accountName,
          accountRole: row.accountRole,
          teamId: row.teamId,
          password: '',
          accountMobile: row.accountMobile,
          email: row.email,
          id: row.id,
        };
      },

      //显示新增界面
      handleAdd: function () {
        this.addOrEditStatus = {
          title: '新增',
          value: 0
        };
        this.accountNameEnable = false;
        this.addOrEditFormVisible = true;
        // 显示新增页面的默认值
        this.addOrEditForm = {
          realName: '',
          accountName: '',
          password: '',
          accountMobile: '',
          teamId: '',
          email: '',
          accountRole: '',
        };
      },

      // 分页
      handleCurrentChange(val) {
        this.page = val;
        this.handleSearch();
      },

      // 查询
      handleSearch() {
        this.listLoading = true;
        let params = {};
        if (this.accountName) {
          params.accountName = this.accountName;
        }
        params.page = this.page;
        params.size = this.size;
        this.getUserPage(params ? params : '')
      },

      // 修改启用 禁用 状态
      changeEnabledState(i, n) {
        let enabledState = '';
        if (n.enabledState === 0) {
          enabledState = 1
        } else if (n.enabledState === 1) {
          enabledState = 0
        }
        let url = baseUrL + '/updateUserEnable';
        this.$http.post(url, {accountId: n.id, enabledState: enabledState}, {emulateJSON: true}).then(function (res) {
          if (res.body.success) {
            this.$message({showClose: true, message: "操作成功", type: 'success'});
            this.handleSearch();
          } else {
            this.$message({showClose: true, message: res.body.message, type: 'error'});
          }
        }).catch(err => {
          console.log('error', err);
          this.$message({showClose: true, message: err, type: 'error'});
        });
      },

      // 删除用户
      deleteUser(i, n) {
        this.$confirm('确认删除该记录吗?', '提示', {type: 'warning'}).then(() => {
          let url = baseUrL + '/deleteUser';
          this.$http.post(url, {accountId: n.id}, {emulateJSON: true}).then(function (res) {
            if (res.body.success) {
              this.addOrEditFormVisible = false;
              this.$message({showClose: true, message: "操作成功", type: 'success'});
              this.handleSearch();
            } else {
              this.$message({showClose: true, message: res.body.message, type: 'error'});
            }
          }).catch((err) => {
            console.log('error', err);
            this.$message({showClose: true, message: err, type: 'error'});
          })
        })
      },
    },

    mounted() {
      this.handleSearch();
      this.getAccountRoleEnum(1);
      this.getAccountRoleEnum(2);
      eventBus.$emit('firstColumn', '基础信息');
      eventBus.$emit('secondColumn', '账号管理');
      eventBus.$emit('toPage', '/account_info');
    },
    watch: {
      // 监听输入框
      accountName(val) {
        // 查询列表数据
        this.handleSearch();
      }
    }
  }
</script>


<style scoped>
  .enabled {
    color: #67C23A;
  }

  .notEnabled {
    color: #FF4949;
  }
</style>

<style>
  .el-table .cell {
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    white-space: normal;
    word-break: break-all;
    line-height: 23px;
    /*color: #303133;*/
  }
</style>

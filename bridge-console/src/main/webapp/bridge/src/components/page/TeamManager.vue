<template>
  <section>
    <el-row class="el-operate-my">
      <!--操作栏-->
      <el-col :span="24" class="toolbar">
        <el-form :inline="true">
          <el-form-item>
            <Input v-model="teamName" placeholder="按团队名称搜索" style="width:200px;" clearable/>
          </el-form-item>
          <el-form-item>
            <el-button size="small" type="primary" icon="el-icon-search" v-on:click="teamSearch">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button size="small" type="primary" icon="el-icon-plus" v-on:click="addTeam">新增</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>

    <el-row :span="24" class="el-table-my">
      <!--列表-->
      <el-table :data="teamListData" stripe fit v-loading="listLoading" style="width: 100%;" size="medium">
        <el-table-column type="index" label="#"/>
        <el-table-column prop="teamName" label="团队名称"/>
        <el-table-column prop="teamDes" label="团队描述"/>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button size="medium" type="text" @click="editTeam(scope.$index, scope.row)">编辑</el-button>
            <Divider type="vertical"/>
            <el-button size="medium" type="text" @click="deleteTeam(scope.$index, scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>


      <!--工具条-->
      <el-col :span="24" class="toolbar">
        <el-pagination layout="total,  prev, pager, next, jumper" @current-change="teamPageChange"
                       :page-count="totalPage" :size="size" :total="total" style="float:right;">
        </el-pagination>
      </el-col>

      <!--dialog-->
      <el-dialog :title="addOrEditStatus.title" :visible.sync="teamAddOrEditFormVisible"
                 :close-on-click-modal="false" size="tiny" top="5%" width="32%">
        <el-form :model="addOrEditForm" ref="addOrEditForm" :rules="rules" label-width="100px">
          <el-form-item label="团队名称" prop="teamName">
            <el-input size="small" v-model="addOrEditForm.teamName"/>
          </el-form-item>
          <el-form-item label="团队描述" prop="teamDes">
            <el-input size="small" v-model="addOrEditForm.teamDes"/>
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
  import eventBus from '../../common/js/eventBus.js'

  let baseUrL = '/bridge';

  export default {
    data() {
      return {
        teamName: '',
        myTeamId: '',
        teamListData: [],
        total: 0,
        totalPage: 0,
        size: 10,
        page: 1,
        listLoading: true,
        teamAddOrEditFormVisible: false,
        addOrEditForm: {},
        addOrEditStatus: {
          title: '新增',
          value: 0,
        },
        rules: {
          teamName: [
            {required: true, message: '请输入团队名称', trigger: 'blur'},
          ],
          teamDes: [
            {required: true, message: '请输入团队描述', trigger: 'blur'}
          ]
        },
      }
    },
    computed: {},
    methods: {

      // 获取用户账号列表
      getTeamPage(params) {
        NProgress.start();
        let url = baseUrL + '/queryTeamDefVOList';
        this.$http.get(url, {params: params}).then(function (res) {
          this.total = res.body.total;
          this.totalPage = res.body.totalPage;
          this.teamListData = res.body.result;
          this.listLoading = false;
        }).catch(err => {
          console.log('error', err)
        });
        NProgress.done();
      },

      // 取消
      cancel() {
        this.teamAddOrEditFormVisible = false;
        this.$refs['addOrEditForm'].clearValidate();
      },

      // 新增或者编辑
      addOrEdit() {
        this.$refs['addOrEditForm'].validate((valid) => {
          if (valid) {
            let url;
            // 新增
            if (this.addOrEditStatus.value === 0) {
              url = baseUrL + '/addTeam';
              // 编辑更新
            } else if (this.addOrEditStatus.value === 1) {
              url = baseUrL + '/editTeam';
            }
            // 发送http请求
            this.$http.post(url, this.addOrEditForm, {emulateJSON: true}).then(function (res) {
              if (res.body.success) {
                this.teamAddOrEditFormVisible = false;
                this.$message({showClose: true, message: "操作成功", type: 'success'});
                this.teamSearch();
              } else {
                this.$message({showClose: true, message: res.body.message, type: 'error'});
                this.teamAddOrEditFormVisible = true
              }
            })
          } else {
            console.log('error submit!!');
            this.$message({showClose: true, message: "表单校验失败", type: 'error'});
            return false;
          }
        });

      },

      // 显示编辑界面
      editTeam: function (index, row) {
        this.addOrEditStatus = {
          title: '编辑',
          value: 1
        };
        this.teamAddOrEditFormVisible = true;
        this.addOrEditForm = {
          teamName: row.teamName,
          teamDes: row.teamDes,
          id: row.id,
        };
      },

      // 显示新增界面
      addTeam: function () {
        this.addOrEditStatus = {
          title: '新增',
          value: 0
        };
        this.teamAddOrEditFormVisible = true;
        this.addOrEditForm = {
          teamName: '',
          teamDes: '',
          id: '',
        };
      },

      // 分页
      teamPageChange(val) {
        this.page = val;
        this.teamSearch();
      },

      // 查询
      teamSearch() {
        this.listLoading = true;
        let params = {};
        if (this.teamName) {
          params.teamName = this.teamName;
        }
        params.page = this.page;
        params.size = this.size;
        this.getTeamPage(params ? params : '')
      },


      // 删除用户
      deleteTeam(i, n) {
        this.$confirm('确认删除该记录吗?', '提示', {type: 'warning'}).then(() => {
          let url = baseUrL + '/deleteTeam';
          this.$http.post(url, {teamId: n.id}, {emulateJSON: true}).then(function (res) {
            if (res.body.success) {
              this.teamAddOrEditFormVisible = false;
              this.$message({showClose: true, message: "操作成功", type: 'success'});
              this.teamSearch();
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
      eventBus.$emit('firstColumn', '基础信息');
      eventBus.$emit('secondColumn', '团队管理');
      eventBus.$emit('toPage', '/team_manager');
      this.teamSearch();
    },
    watch: {
      // 监听输入框
      teamName(val) {
        // 查询列表数据
        this.teamSearch();
      }
    }
  }
</script>

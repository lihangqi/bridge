<template>
  <section>
    <div class="header">
      <div class="logo" v-show="sidebarStatus === 'expand'">
        <div class="collapse-btn gold" style="text-align: center">
          <i :class="!isCollapse?'el-icon-d-arrow-left':'el-icon-d-arrow-right'"></i>
        </div>
        <span class="gold" style="font-size: 16px">配置中心 · 管理系统</span>
        <span class="gold" style="font-size: 14px;margin-left: 20px;color: #909399">v {{version}}</span>
      </div>

      <div class="user-info">
        <el-dropdown size="small" trigger="click" @command="handleCommand">
          <span class="el-dropdown-link gold">
            <img class="user-logo" src="https://dev-file.iviewui.com/avatar_default/avatar">{{realName}}
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>

      <div class="env-info">
        <el-dropdown size="small" trigger="click" @command="handleEvnCommand">
          <span class="el-dropdown-link gold">
            {{env}}
            <i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="dev">开发环境</el-dropdown-item>
            <el-dropdown-item command="test" divided>测试环境</el-dropdown-item>
            <el-dropdown-item class="warn" command="stable" divided>预发环境</el-dropdown-item>
            <el-dropdown-item class="danger" command="online" divided>生产环境</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>


    <el-dialog title="修改密码" :visible.sync="dialogFormVisible" :close-on-click-modal="false" width="32%" size="tiny"
               top="5%">
      <el-form :model="ruleForm2" :rules="rules2" ref="ruleForm2" label-width="100px" class="demo-ruleForm">
        <el-form-item label="账号" prop="userName">
          <span>{{ruleForm2.userName}}</span>
        </el-form-item>
        <el-form-item label="旧密码" prop="oldPass">
          <el-input size="small" type="password" v-model="ruleForm2.oldPass" auto-complete="off"/>
        </el-form-item>
        <el-form-item label="密码" prop="pass">
          <el-input size="small" type="password" v-model="ruleForm2.pass" auto-complete="off"/>
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPass">
          <el-input size="small" type="password" v-model="ruleForm2.checkPass" auto-complete="off"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="small" @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="small" @click="changePassword">确 定</el-button>
      </div>
    </el-dialog>

  </section>
</template>

<script>
  let baseUrL = '/bridge';
  import eventBus from '../../common/js/eventBus.js'

  export default {
    data() {
      let validatePass = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请输入密码'));
        } else {
          if (this.ruleForm2.checkPass !== '') {
            this.$refs.ruleForm2.validateField('checkPass');
          }
          callback();
        }
      };
      let validatePass2 = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请再次输入密码'));
        } else if (value !== this.ruleForm2.pass) {
          callback(new Error('两次输入密码不一致!'));
        } else {
          callback();
        }
      };
      return {
        userId: '',
        env: '',
        toPage: '',
        dialogFormVisible: false,
        sidebarStatus: 'expand',
        isCollapse: false,
        version : '',
        ruleForm2: {
          userId: '',
          userName: '',
          oldPass: '',
          pass: '',
          checkPass: '',
        },
        rules2: {
          oldPass: [
            {required: true, message: '请输入旧密码', trigger: 'blur'}
          ],
          pass: [
            {required: true, validator: validatePass, trigger: 'blur'}
          ],
          checkPass: [
            {required: true, validator: validatePass2, trigger: 'blur'}
          ]
        },
      }
    },
    computed: {
      username() {
        let username = localStorage.getItem('ms_username');
        return username ? username : '';
      },
      realName() {
        let realName = localStorage.getItem('realName');
        return realName ? realName : '';
      }
    },
    created() {
      this.userId = localStorage.getItem('userId');
      if (!this.userId > 0) {
        this.handleCommand('logout')
      }
      this.env = localStorage.getItem('env');
      if (!this.env) {
        this.handleEvnCommand('dev');
      }
    },
    methods: {

      // 环境设置
      handleEvnCommand(command) {
        if (command === 'dev') {
          this.env = '开发环境';
          localStorage.setItem('envId', '0');
          localStorage.setItem('env', this.env);
          eventBus.$emit('change');
          return;
        }
        if (command === 'test') {
          this.env = '测试环境';
          localStorage.setItem('envId', '1');
          localStorage.setItem('env', this.env);
          eventBus.$emit('change');
          return;
        }
        if (command === 'stable') {
          this.env = '预发环境';
          localStorage.setItem('envId', '2');
          localStorage.setItem('env', this.env);
          eventBus.$emit('change');
          return;
        }
        if (command === 'online') {
          this.env = '生产环境';
          localStorage.setItem('envId', '3');
          localStorage.setItem('env', this.env);
          eventBus.$emit('change');
          return;
        }
      },

      handleCommand(command) {
        // 退出登录
        if (command === 'logout') {
          this.logout();
        }
        // 修改密码
        if (command === 'changePassword') {
          console.log("changePassword");
          this.ruleForm2.userName = localStorage.getItem('ms_username');
          this.ruleForm2.userId = localStorage.getItem('userId');
          this.dialogFormVisible = true;
        }
      },

      // 退出登录
      logout() {
        let url = baseUrL + '/logout';
        let params = {};
        this.$http.get(url, params, {emulateJSON: true})
          .then(function (res) {
            if (res.body.success) {
              this.$message({showClose: true, message: "退出成功", type: 'success'});
              localStorage.clear();
              this.$router.push('/login')
            } else {
              this.$message({
                showClose: true,
                message: (res.body.message ? res.body.message : '请重新登录'),
                type: 'error'
              });
            }
          }).catch(err => {
          // 请求数据异常的请求
          this.$message({showClose: true, message: err, type: 'error'});
          console.log('error', err)
        });
      },

      queryVersion() {
        let url = baseUrL + '/getSysVersion';
        let params = {};
        this.$http.get(url, params, {emulateJSON: true})
          .then(function (res) {
            if (res.body.success) {
              this.version = res.body.result;
            }
          }).catch(err => {
          console.log('error', err)
        });
      },

      // 修改密码
      changePassword() {
        let url = baseUrL + '/changePassword';
        let params = {};
        params.oldPassword = this.ruleForm2.oldPass;
        params.newPassword = this.ruleForm2.pass;
        params.confirmPwd = this.ruleForm2.pass;
        this.$http.post(url, params, {emulateJSON: true}).then(function (res) {
          if (res.body.success) {
            this.dialogFormVisible = false;
            this.$refs.ruleForm2.resetFields();
            this.$message({showClose: true, message: '修改成功, 请重新登录', type: 'success'});
            // 修改密码后重新登录
            // handleCommand('logout');
          } else {
            this.$message({showClose: true, message: res.body.message, type: 'error'});
          }
        }).catch(err => {
          // 请求数据异常
          this.$message({showClose: true, message: 'API请求报错', type: 'error'});
          console.log('error', err)
        });
      },
      changeCollapse() {
        this.isCollapse = !this.isCollapse;
        eventBus.$emit('isCollapse', this.isCollapse)
      }
    },
    mounted() {
      let that = this;
      eventBus.$on('toPage', function (toPage) {
        that.toPage = toPage;
      });
      this.queryVersion();
    },
  }
</script>
<style scoped>
  .header {
    position: relative;
    box-sizing: border-box;
    width: 100%;
    height: 64px;
    background: linear-gradient(90deg, #1d1e23, #3f4045);
    line-height: 64px;
    font-size: 22px;
    border-bottom: 1px solid rgb(14, 14, 18);
  }

  .header .logo {
    float: left;
    text-align: center;
    margin: 0 10px;
    font-size: 16px;
  }

  .user-info {
    float: right;
    padding-right: 80px;
    font-size: 16px;
  }

  .env-info {
    float: right;
    padding-right: 50px;
    font-size: 16px;
  }

  .user-info .el-dropdown-link {
    position: relative;
    display: inline-block;
    height: 64px;
    padding-left: 48px;
    cursor: pointer;
    vertical-align: middle;
  }

  .user-info .user-logo {
    position: absolute;
    left: 0;
    top: 5px;
    margin-top: 13px;
    margin-left: 15px;
    width: 25px;
    height: 25px;
    border-radius: 50%;
  }

  .collapse-btn {
    margin-left: 5px;
    margin-right: 20px;
    float: left;
  }

  .el-dropdown-link {
    cursor: pointer;
  }

  .el-icon-arrow-down {
    font-size: 12px;
  }
</style>

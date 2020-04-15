<template>
  <div class="login-page">
    <div class="login-card">
      <Card class="main">
        <h2 class="title">Bridge · 管理系统</h2>
        <Form ref="loginValidate" :model="loginValidate" :rules="ruleValidate" label-position="top"
              class="submit-form">
          <Form-item label="账号：" prop="accountName">
            <Input size="large" v-model="loginValidate.accountName" clearable/>
          </Form-item>
          <Form-item label="密码：" prop="password">
            <Input type="password" size="large" v-model="loginValidate.password" clearable/>
          </Form-item>
          <div class="sub text-center">
            <Button size="large" class="login-btn" type="primary" long :loading="sending"
                    @click="handleSubmit('loginValidate')">登&nbsp;&nbsp;录
            </Button>
          </div>
<!--          <div class="pro">-->
<!--            <div class="direct"/>-->
<!--          </div>-->
          <div class="others text-center">
            <div class="text">Hi, 欢迎登录 配置中心·管理系统</div>
            <div class="text" style="padding-top: 2px">
              <a href="http://doc.u2open.com/zh-cn/" target="_blank" style="font-size: 10px">前往阅读文档 >></a>
            </div>
          </div>
        </Form>
      </Card>
    </div>
    <div class="footer">
      <a href="http://www.beian.miit.gov.cn/" target="_blank" style="color: #ffffff">浙ICP备17049891号-1</a>
      <a href="http://www.beian.miit.gov.cn/" target="_blank" style="color: #ffffff;margin-left: 20px">浙ICP备17049891号-2</a>
    </div>
  </div>
</template>

<script type="es6">

  export default {

    data() {
      const validateName = (rule, value, callback = function () {
      }) => {
        if (!value) {
          return callback(new Error('请填写有效账号'));
        }
        callback();
      };
      const validatePassword = (rule, value, callback = function () {
      }) => {
        if (!value) {
          return callback(new Error('密码不少于6位'));
        }
        callback();
      };
      return {
        sending: false,
        loginValidate: {
          accountName: '',
          password: '',
          remember: false
        },
        ruleValidate: {
          accountName: [
            {validator: validateName, trigger: 'blur'}
          ],
          password: [
            {validator: validatePassword, trigger: 'blur'}
          ]
        }
      }
    },
    created() {

    },
    methods: {

      // 登录
      handleSubmit(params) {
        const self = this;
        self.$refs[params].validate((valid) => {
          if (valid) {
            let param = {};
            param.accountName = this.loginValidate.accountName;
            param.password = this.loginValidate.password;
            this.postLogin(param);
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },

      // 获取URL的参数值
      getQueryString: function (name) {
        let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        let r = window.location.search.substr(1).match(reg);
        if (r != null)
          return decodeURIComponent(r[2]);
        return null;
      },

      // 执行登陆
      postLogin(params) {
        let url = '/bridge/login', self = this;
        this.$http.get(url, {params})
          .then(function (res) {
            if (res.body.success) {
              let userId = res.body.result.id;
              let token = res.body.result.token;
              // 缓存相关信息
              localStorage.setItem('ms_username', self.loginValidate.accountName);
              localStorage.setItem('token', token);
              localStorage.setItem('userId', userId);
              localStorage.setItem('realName', res.body.result.realName);
              localStorage.setItem('userTeamName', res.body.result.teamName);
              localStorage.setItem('accountRole', res.body.result.accountRole);
              localStorage.setItem('permissionList', res.body.result.permissionList);
              console.log(localStorage);
              self.$router.push('/welcome');
            } else {
              this.$message({showClose: true, message: res.body.message, type: 'error'});
              this.$router.push('/login');
            }
          }).catch(err => {
          // 请求数据异常的请款
          this.$message({showClose: true, message: 'API请求报错', type: 'error'});
          console.log('error', err);
          this.$router.push('/login');
        });
      },

    },
  }
</script>


<style>
  .login-page {
    /*background: url("/static/img/head_pic.png") no-repeat center;*/
    /*!*background: #FFF;*!*/
    height: 100%;
    position: absolute;
    width: 100%;
    margin: 0;
    background: url("http://acxnn.oss-cn-hangzhou.aliyuncs.com/bridge/bg.jpg") no-repeat center;
    /*background: url("../../../static/789.jpg") no-repeat center;*/
    background-size: cover;
    /*text-align: center;*/
  }

  .login-card {
    /*width: 840px;*/
    /*height: 482px;*/
    /*margin: 200px auto;*/
    /*padding-left: 200px;*/
    /*border-radius: 0px;*/
    /*box-shadow: 0 0 4px rgba(0,0,0,.99);*/
    /*line-height: 1;*/
    /*background: url("/static/img/1231.png") no-repeat center;*/
    /*background:rgba(255,255,255,0.1);*/
  }

  .login-card .main {
    float: left;
    border: 0 solid #e5e5e5;
    border-radius: 0;
    background-color: #fff;
    box-shadow: 2px 2px 56px rgba(0, 0, 0, 0.88);
    -webkit-box-shadow: 2px 2px 56px rgba(0, 0, 0, .88);
    width: 450px;
    height: 564px;
    top: 160px;
    padding-left: 55px;
    margin-left: 960px;

  }

  .login-card .main .title {
    margin: 50px 0 0;
    color: #212121;
    font-size: 30px;
    font-weight: 300;
    padding-left: 35px;
  }

  .login-card .main .submit-form {
    margin-top: 45px;
    width: 315px;
  }

  .login-card .main {
    position: relative;
  }

  .sub {
    margin-top: 56px;
    padding-bottom: 20px;
  }

  .submit-form .pro {
    text-align: center;
    font-size: 12px;
    color: #666;
    letter-spacing: 1px;
    margin-bottom: 56px;
  }

  .main .others {
    padding-top: 18px;
    border-top: 1px solid #e5e5e5;
    color: #ccc;
    font-size: 13px;
    letter-spacing: 1px;
    cursor: pointer;
  }

  .others .party-link {
    width: 198px;
    margin: 15px auto 0;
  }

  .login-btn {
    padding-bottom: 6px;
    font-size: 16px;
  }

  input:-webkit-autofill {
    -webkit-box-shadow: 0 0 100000px #fff inset !important;
    -webkit-text-fill-color: #333;
  }

  .footer{
    height: 30px;
    width: 100%;
    background-color: rgba(255,255,255,0);
    position: fixed;
    bottom: 0;
    text-align: center;
  }
</style>

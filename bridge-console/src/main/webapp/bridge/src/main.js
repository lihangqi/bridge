// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
// import ElementUI from 'element-ui';
// import locale from 'iview/dist/locale/en-US';

// 默认主题
// import 'element-ui/lib/theme-chalk/index.css';
// import VueRouter from 'vue-router'
// import NProgress from 'nprogress'
// import VueResource from 'vue-resource'
// import 'nprogress/nprogress.css'
// import Icon from 'vue-svg-icon/Icon.vue'
// import iView from 'iview'
// import 'iview/dist/styles/iview.css'
// import Antd from "ant-design-vue";
// import "ant-design-vue/dist/antd.css";

// Vue.use(Antd);
// Vue.use(iView, {locale});
// Vue.use(iView);
// Vue.use(VueResource);
// Vue.use(ElementUI);
// Vue.use(VueRouter);
// Vue.component('icon', Icon);

// NProgress.configure({showSpinner: false, speed: 700});
Vue.http.options.emulateJSON = true;

Vue.config.productionTip = false;

// 不重定向白名单
const whiteList = ['/login'];

router.beforeEach((to, from, next) => {
  // 判断是否有token
  if (localStorage.getItem('token')) {
    if (to.path === '/login') {
      next({path: '/'});
    } else {
      next();
    }
  } else {
    // 在免登录白名单，直接进入
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      // 否则全部重定向到登录页
      next('/login')
    }
  }
});


Vue.http.interceptors.push(function (request, next) {
  request.headers.set('token', localStorage.getItem('token'));
  next(function (response) {
    console.log('response=>', response);
    if (response.body.code === -10086) {
      localStorage.clear();
      this.$message({
        showClose: true,
        showIcon: true,
        title: '警告',
        duration: '5000',
        message: '您的账号信息发生变化, 请重新登录',
        type: 'warning'
      });
      this.$router.push('/login')
    }
    if (response.body.code === -10087) {
      // this.$message({
      //   showClose: true,
      //   showIcon: true,
      //   duration: '5000',
      //   message: '账号权限不够, 无法操作',
      //   type: 'info'
      // });
      this.$router.push('/403')

    }
  })
});

new Vue({
  router,
  render: h => h(App)
}).$mount('#app');

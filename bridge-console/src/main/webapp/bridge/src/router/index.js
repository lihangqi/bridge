import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);

export default new Router({

  base: '/bridge',

  routes: [
    {
      path: '/',
      redirect: '/welcome',
    },
    {
      path: '/readme',
      component: resolve => require(['../components/common/Home.vue'], resolve),
      children: [
        {   // 健康分析
          path: '/health_analysis',
          component: resolve => require(['../components/page/ZookeeperDataManager.vue'], resolve)
        },
        {   // 系统日志
          path: '/system_log',
          component: resolve => require(['../components/page/SystemLog.vue'], resolve)
        },
        {   // 系统管理
          path: '/system_manager',
          component: resolve => require(['../components/page/AppManager.vue'], resolve)
        },
        {   // 团队管理
          path: '/team_manager',
          component: resolve => require(['../components/page/TeamManager.vue'], resolve)
        },
        {
          path: '/welcome',
          component: resolve => require(['../components/page/WorkSpaceManager.vue'], resolve)
        },
        {
          path: '/account_info',
          component: resolve => require(['../components/page/AccountManager.vue'], resolve)
        },
        {
          path: '/config_manager',
          component: resolve => require(['../components/page/ConfigManager.vue'], resolve)
        },
        {
          path: '/devDoc_manager',
          component: resolve => require(['../components/page/DevDocManager.vue'], resolve)
        },
        {
          path: '/operateLog_manager',
          component: resolve => require(['../components/page/OperateLogManager.vue'], resolve)
        },
        {
          path: '/bug_feedBack',
          component: resolve => require(['../components/page/BugFeedBackManager.vue'], resolve)
        },
        {
          path: '/403',
          component: resolve => require(['../components/page/403.vue'], resolve)
        }
      ]
    },
    {
      path: '/login',
      component: resolve => require(['../components/page/Login.vue'], resolve)
    },
  ]
})

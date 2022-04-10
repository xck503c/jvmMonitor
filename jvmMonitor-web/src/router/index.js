import Vue from 'vue'
import Router from 'vue-router'
import Center from '@/components/Center'
import Form from '@/components/Form'
import MethodMonitorRule from '@/components/methodMonitor/MethodMonitorRule'
import MethodMonitorRuleGroupLayer from '@/components/methodMonitor/MethodMonitorRuleGroupLayer'
import ProcessonMonitorRule from '@/components/methodMonitor/ProcessonMonitorRule'
import Register from '@/components/Register'
import RespLayer from '@/components/RespLayer'

Vue.use(Router)

export default new Router({
  // mode: 'history',
  // mode: 'hash',
  routes: [
    {
      path: '/Form',
      name: 'Form',
      component: Form
    },
    {
      path: '/',
      name: 'Center',
      component: Center,
      redirect: {name: "Register"},
      children: [
        {
          path: '/Register',
          name: 'Register',
          component: Register
        },
        {
          path: '/MethodMonitorRule',
          name: 'MethodMonitorRule',
          component: MethodMonitorRule
        }
      ]
    },
    {
      path: '/RespLayer',
      name: 'RespLayer',
      component: RespLayer
    },
    {
      path: '/MethodMonitorRuleGroupLayer',
      name: 'MethodMonitorRuleGroupLayer',
      component: MethodMonitorRuleGroupLayer
    },
    {
      path: '/ProcessonMonitorRule',
      name: 'ProcessonMonitorRule',
      component: ProcessonMonitorRule
    }
  ]
})

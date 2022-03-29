import Vue from 'vue'
import Router from 'vue-router'
import Form from '@/components/Form'
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
      name: 'Register',
      component: Register
    },
    {
      path: '/RespLayer',
      name: 'RespLayer',
      component: RespLayer
    }
  ]
})

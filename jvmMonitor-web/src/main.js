// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'

// 引入element-ui UI框架
import ElementUI from "element-ui"
import 'element-ui/lib/theme-chalk/index.css'

// 引入form-create 表单生成器
import formCreate from "@form-create/element-ui"

import axios from 'axios'
import VueAxios from 'vue-axios'
// Vue.use(axios) axios不能用use 只能修改原型链
Vue.prototype.$axios = axios;

import JsonViewer from 'vue-json-viewer'
import 'vue-json-viewer/style.css'

import layer from 'vue-layer'
import 'vue-layer/lib/vue-layer.css'

Vue.prototype.$layer = layer(Vue);

// 全局注入(挂载)element-ui和form-create
Vue.use(ElementUI)
Vue.use(formCreate)
Vue.use(VueAxios)
Vue.use(JsonViewer)

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})

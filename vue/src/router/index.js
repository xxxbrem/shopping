import Vue from 'vue'
import VueRouter from 'vue-router'
import UserView from '../views/manage/UserView.vue'
import Layout from "@/layout/Layout.vue";
import ShopView from "@/views/shop/ShopView.vue";
import MyLayout from "@/layout/MyLayout.vue";
import LoginView from "@/views/LoginView.vue";
import ShoppingCart from "@/views/shop/ShoppingCart.vue";
import MyOrder from "@/views/shop/MyOrder.vue";

Vue.use(VueRouter)

const routes = [
  //   管理路由
  {
    //当一个框架有默认的子路由的时候就不需要名称，将子路由写为'/'就可以了
    path: '/home',
    component: Layout,
    // 管理子路由
    children: [{
      path: '/',
      alias: '/user',
      name: 'user',
      component: UserView,
    },{
      path: '/person',
      name: 'person',
      component: () => import("@/views/manage/PersonView.vue")
    },{
      path: '/item',
      name: 'item',
      component: () => import("@/views/manage/ItemView.vue")
    },{
      path: '/order',
      name: "order",
      component: () => import("@/views/manage/OrderView.vue")
    },{
      path: '/description',
      name: "description",
      component: () => import("@/views/manage/Description.vue")
    }]
  },
  //   登录路由
  {
    path: '/',
    alias: '/login',
    name: 'Login',
    component: LoginView,
  },
  //   注册路由
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  //   商城路由
  {
    path: '/shop',
    component: () => import('@/layout/ShopLayout.vue'),
    // 商城子路由
    children: [{
      path: '/',
      name: 'shop',
      component: ShopView,
    },{
      path: '/shop/detail',
      name: 'detail',
      component: () => import('@/views/shop/Detail.vue')
    },{
      path: '/mine',
      component: MyLayout,
      // 我的淘宝子路由
      children: [{
        path: '/',
        name: 'profile',
        component: () => import("@/views/shop/ProfileView.vue")
      },{
        path: '/cart',
        name: 'ShoppingCart',
        component: ShoppingCart
      },{
        path: '/myOrder',
        name: "myOrderView",
        component: MyOrder
      }]
    }]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router

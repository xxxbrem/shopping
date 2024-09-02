<template>
  <body>
  <div class="box">
    <h2 style="color: rgba(251,244,245);">Login</h2>
    <div class="input-box">
      <label>账号</label>
      <input type="text" v-model="form.username" placeholder="请输入用户名">
    </div>
    <div class="input-box">
      <label>密码</label>
      <!--      这里就不需要用.native来回传给父组件了，因为如果是el标签，相当于输入框是一个子组件，在里面的发生的事件要用.native绑定父组件，这里直接就是一个原生组件-->
      <input type="password" @keydown.enter="login()" v-model="form.password" placeholder="请输入密码">
    </div>
    <div class="btn-box">
      <a @click="$message.warning('请联系管理员修改密码')">忘记密码？</a>
      <div>
        <button @click="$router.push('/register')">注册</button>
        <button @click="login()">登录</button>
      </div>
    </div>
  </div>
  </body>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "LoginView",
  created() {
    document.title = '欢迎登录';
  },
  data() {
    return {
      form: {}
    }
  },
  methods: {
    login() {
      request.post('/user/login', this.form).then(res => {
        if (res.code === '0') {
          this.$message({
            type: 'success',
            message: "登录成功",
          });
          // 缓存用户信息
          sessionStorage.setItem("user", JSON.stringify(res.data));
          // 跳转主页，检测用户的用户名是否为admin，如果是admin才跳转管理页面
          if (res.data.username === 'admin')
            this.$router.push('/user');
          else
            this.$router.push('/shop');
        } else {
          this.$message({
            type: 'error',
            message: res.msg,
          })
        }
      })
    }
  }
}
</script>

<style src="../assets/css/login.css" scoped>
</style>
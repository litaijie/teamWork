<style lang="less">
  @import './login.less';
</style>

<template>
  <div class="login">
    <div class="login-con">
      <Card icon="log-in" title="欢迎登录" :bordered="false">
        <div class="form-con">
          <login-form @on-success-valid="handleSubmit"></login-form>
          <p class="login-tip" v-if="showTip" style="color: crimson">{{this.tipText}}</p>
        </div>
      </Card>
    </div>
  </div>
</template>

<script>
import LoginForm from '_c/login-form'
import { mapActions } from 'vuex'
export default {
  components: {
    LoginForm
  },
  data(){
    return {
      tipText: null,
      showTip: false
    }
  },
  methods: {
    ...mapActions([
      'handleLogin',
      'getUserInfo'
    ]),
    handleSubmit ({ userName, password }) {
      this.handleLogin({ userName, password }).then(res=>{
        if (res.code===0){
          this.showTip=false
          this.$router.push({
            name: this.$config.homeName
          })
        }else {
          this.showTip=true
          this.tipText=res.msg
        }
      })
    }
  }
}
</script>

<style>

</style>

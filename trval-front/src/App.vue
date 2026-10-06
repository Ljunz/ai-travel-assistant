<script setup>
import {ref} from 'vue'
import {useRoute} from 'vue-router'

const active = ref(0)
const route = useRoute()

//添加路由监听，建立路由变化与 active 更新的 响应式关联
import {watch} from 'vue'
import {useRouter} from 'vue-router'
const router = useRouter()

watch(() => router.currentRoute.value.path, (newPath) => {
  switch (newPath) {
    case '/':
      active.value = 0
      break
    case '/chat':
      active.value = 1
      break
    case '/profile':
      active.value = 2
      break
    default:
      active.value = 0
  }
})
</script>

<template>
  <router-view />
  <van-tabbar v-if="['Home', 'Chat', 'Profile'].includes(route.name)" v-model="active">
    <van-tabbar-item to="/" icon="home-o">首页</van-tabbar-item>
    <van-tabbar-item to="/chat" icon="chat-o">对话</van-tabbar-item>
    <van-tabbar-item to="/profile" icon="user-o">我的</van-tabbar-item>
  </van-tabbar>


</template>

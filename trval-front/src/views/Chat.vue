<template>
    <div class="page-container chat-page">
        <div class="page-header" style="height: 46px;">
            <van-nav-bar title="AI旅游助手" left-arrow left-text="返回" fixed @click="goBack"/>
        </div>
        <div class="chat-container" ref="chatContainer">
            <div v-if="messages.length === 0" class="chat-empty">
                <van-empty description="开始和AI旅游助手对话" />
                <div class="quick-questions">
                    <div class="quick-title">常见问题</div>
                    <van-tag class="quick-tag" v-for="question in quickQuestions" :key="question" mark size="large">{{ question }}</van-tag>
                </div>
            </div>
            <div v-else class="message-list">
                <ChatBubble v-for="msg in messages" :key="msg.id" :message="msg" />
                <div class="streaming-indicator" v-if="isStreaming">
                    <van-loading type="spinner" size="20px" />
                    <span>AI正在思考中...</span>
                </div>
            </div>
        </div>
        <div class="chat-input-area">
            <van-cell-group inset>
                <van-field v-model="inputMessage" placeholder="输入您的问题" :disabled="isStreaming">
                    <template #button>
                        <van-button @click="sendMessage" size="small" type="primary" :disabled="!inputMessage.trim()">发送</van-button>
                    </template>
                </van-field>
            </van-cell-group>
        </div>
    </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import {ref} from 'vue'
import {fetchStream} from '../utils/request'
import {showToast} from 'vant'
import ChatBubble from '../components/ChatBubble.vue'

//添加置底操作
const chatContainer = ref(null)
const scrollToBottom = () => {
    if(chatContainer.value){
        chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
}

const router = useRouter()
const inputMessage = ref('')
const isStreaming = ref(false)
const messages = ref([])
const quickQuestions = ref([
  '北京有哪些必去的景点？',
  '上海美食推荐',
  '成都三日游攻略',
  '如何选择旅行保险？'
])

const goBack = () => {
    router.back()
}
const sendMessage = () => {
    const msg = inputMessage.value.trim()
    if(!msg || isStreaming.value){
      return
    }
    
    addUserMessage(msg)
    inputMessage.value = ''
    fetchAIResponse(msg)
}

const addUserMessage = (msg) => {
  messages.value.push({
    id:Date.now()+1,
    role:'user',
    content:msg,
    timestamp:new Date().toISOString()
  })
}

//获取流式响应
const fetchAIResponse = (msg) => {
    isStreaming.value = true
    messages.value.push({
        id:Date.now()+1,
        role:'ai',
        content:'',
        timestamp:new Date().toISOString()
    })

    let fullResponse = ''

    fetchStream('chat',{message:msg},(chunk) => {
        //AI正在回复
      //console.log(chunk)
      fullResponse += chunk
      const lastMsg = messages.value[messages.value.length-1]
      if(lastMsg && lastMsg.role === 'ai'){
        lastMsg.content = fullResponse
      }
      scrollToBottom()
    },() => {
        //AI回复完成
        isStreaming.value = false
        scrollToBottom()
    },(errMsg) => {
        //AI回复失败
        const lastMsg = messages.value[messages.value.length-1]
        if(lastMsg && lastMsg.role === 'ai'){
          lastMsg.content = `抱歉，AI发生错误：${errMsg}`
        }
        isStreaming.value = false
        showToast('AI回复失败，请稍后重试')
        scrollToBottom()
    })
}

</script>

<style scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding-bottom: 50px;
}

.chat-container {
  /* flex: 1; */
  height:630px;
  overflow-y: auto;
  padding: 16px;
  padding-bottom: 60px;
}

.chat-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.quick-questions {
  margin-top: 32px;
  text-align: center;
}

.quick-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 16px;
}

.quick-tag {
  margin: 8px;
  cursor: pointer;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.streaming-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  color: #999;
  font-size: 14px;
}

.chat-input-area {
  position: fixed;
  bottom: 50px;
  left: 0;
  right: 0;
  background: #fff;
  padding: 8px 16px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  max-width: 750px;
  margin: 0 auto;
}

.chat-input-area :deep(.van-field) {
  background: #f7f8fa;
  border-radius: 20px;
  padding: 8px 16px;
}
</style>
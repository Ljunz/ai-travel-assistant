<template>
    <div class="page-container">
        <van-nav-bar title="智能旅游助手" />
        <div class="page-content">
            <van-notice-bar
                left-icon="volume-o"
                text="基于AI的智能景点介绍与行程规划系统"
            />
            <div class="card" style="margin-top: 16px;">
                <div class="section-title">
                    规划你的旅程
                </div>
                <div>
                    <van-field 
                        @click="showCityPicker = true"
                        v-model="formData.city"
                        label="目的地" 
                        is-link
                        placeholder="请选择城市"
                        readonly
                        style="background-color: #f7f7f7;border-radius: 8px;margin-bottom:8px"
                    />
                    <van-field 
                        v-model="formData.budget"
                        label="预算（元）"
                        placeholder="请输入预算金额"
                        style="background-color: #f7f7f7;border-radius: 8px;margin-bottom:8px"
                    />
                    <van-field 
                        v-model="formData.days"
                        label="天数" 
                        placeholder="请输入天数"
                        style="background-color: #f7f7f7;border-radius: 8px;margin-bottom:8px"
                    />
                    <van-button type="primary" size="large" round @click="handlesubmit">规划行程</van-button>
                </div>
            </div>
            <div class="card">
                <div class="section-title">
                    快捷入口
                </div>
                <van-grid :column-num="2" :border="false">
                    <van-grid-item @click="goPage('/chat')" icon="chat-o" text="AI 对话" />
                    <van-grid-item @click="goPage('/profile')" icon="user-o" text="我的" />
                </van-grid>
            </div>
            <div class="card">
                <div class="section-title">
                    热门景点
                </div>
                <van-grid :column-num="4" :border="false" :gutter="12">
                    <van-grid-item @click="selectedCity(city)" v-for="city in hotCities" :key="city">
                        <van-tag size="large" round :text-color="city === formData.city ? '#fff' : '#666'" :color="city === formData.city ? '#1989fa' : '#f7f7fa'">{{city}}</van-tag>
                    </van-grid-item>
                </van-grid>
            </div>
        </div>
        <van-popup 
            v-model:show="showCityPicker" 
            position="bottom"
            round
            :style="{ height: '30%' }"
        >
            <van-picker
                title="标题"
                :columns="columns"
                @confirm="onConfirm"
                @cancel="onCancel"
                @change="onChange"
            />
        </van-popup>
    </div>
</template>

<script setup>
import {ref,reactive} from 'vue'
import {useRouter} from 'vue-router'
import { showToast } from 'vant';



const router = useRouter()

const formData = reactive({
    "city": "",
    "budget": null,
    "days": null
})

const showCityPicker = ref(false)

const allCities = [
  '北京', '上海', '广州', '深圳', '成都', '杭州', '西安', '重庆',
  '南京', '武汉', '苏州', '长沙', '天津', '郑州', '济南', '青岛',
  '大连', '沈阳', '哈尔滨', '长春', '福州', '厦门', '南昌', '合肥',
  '昆明', '贵阳', '南宁', '桂林', '海口', '三亚', '丽江', '大理',
  '西安', '兰州', '乌鲁木齐', '拉萨', '呼和浩特', '太原', '石家庄'
]
const columns = allCities.map(city => ({text:city,value:city}) )
const hotCities = ['北京','上海','深圳','广州','成都','杭州','西安','重庆']

const onCancel = () => {
    showCityPicker.value=false;
}
const onConfirm = ({selectedOptions}) => {
    //console.log(selectedOptions)
    formData.city = selectedOptions[0].text
    showCityPicker.value=false;
}
const goPage = (path) => {
    console.log(router.currentRoute.value.path)
    router.push(path)
}
const selectedCity = (city) => {
    formData.city = city
}
const handlesubmit = () => {
    console.log("规划提交")
    //表单校验
    if(!formData.city){
        showToast('请选择目的地')
        return
    }
    if(!formData.budget || formData.budget < 100){
        showToast('预算不能低于100元')
        return
    }
    if(!formData.days || formData.days < 1 || formData.days > 30){
        showToast('天数必须在1到30天之间')
        return
    }
    console.log(formData)

    //跳转并在url上携带参数
    router.push({
        path:'/detail',
        query:{
            city:formData.city,
            budget:formData.budget,
            days:formData.days
        }
    })
}
</script>


<script setup>
</script>

<style scoped>
.page-container{
    min-height:100vh;
    background-color: #f5f5f5;
    padding-bottom:70px;
}
.page-content{
    padding:16px;
}
.card{
    background-color: #fff;
    border-radius: 8px;
    padding: 16px;
    margin-bottom:15px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.section-title{
    font-size:18px;
    font-weight:600;
    color:#323233;
    margin-bottom:12px;
}
</style>
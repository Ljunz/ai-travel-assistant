<template>
    <div class="page-container">
        <van-nav-bar title="推荐结果" left-arrow @click="goBack"/>
        <div class="page-content">
            <div v-if="loading" class="loading-container">
                <van-loading size="30px" type="spinner" color="#1989fa">数据加载中...</van-loading>
            </div>
            <div v-else-if="error">
                <van-empty description="推荐失败">
                    <van-button @click="fetchRecommend" round type="primary" class="bottom-button">按钮</van-button>
                </van-empty>
            </div>
            <template v-else-if="tripData && tripData.success">
                <div class="overview-card">
                    <h2>{{ tripData.city }} · {{ tripData.days }}天的行程</h2>
                </div>
                <van-collapse style="margin-bottom: 16px;" v-model="activeDays">
                    <van-collapse-item 
                        v-for="day in tripData.dailyItinerary" 
                        :key="day.day" 
                        :title="`第${day.day}天`" 
                        :name="day.day"
                    >
                       <div class="day-schedule">
                            <div class="schedule-section">
                                <div class="section-label morning">上午</div>
                                <SpotItem :data="day.morning" />
                            </div>
                            <div class="schedule-section">
                                <div class="section-label afternoon">下午</div>
                                <SpotItem :data="day.afternoon" />
                            </div>
                            <div class="schedule-section">
                                <div class="section-label evening">晚上</div>
                                <SpotItem :data="day.evening" />
                            </div>
                       </div>
                    </van-collapse-item>
                </van-collapse>
                <div class="card budget-card">
                    <div class="card-header">
                        <div class="section-title">预算明细</div>
                        <BudgetTable :data="tripData.budgetBreakdown" :total="tripData.totalBudget" :days="tripData.days" />
                    </div>
                </div>
                <div class="card tips-card">
                    <div class="section-title">温馨提示</div>
                    <ul class="tips-list">
                        <li v-for="tip in tripData.tips" :key="tip">{{ tip }}</li>
                    </ul>
                </div>
                <div class="card warnings-card">
                    <div class="section-title">注意事项</div>
                    <ul class="warnings-list">
                        <li v-for="warning in tripData.warnings" :key="warning">{{ warning }}</li>
                    </ul>
                </div>
            </template>
        </div>
        <div v-if="tripData && tripData.success" class="detail-footer">
            <van-button type="primary" size="large" round @click="geToChat" class="primary-button">咨询 AI 助手</van-button>
        </div>
    </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { recommendTravel } from '../utils/api'
import SpotItem from '../components/SpotItem.vue'
import BudgetTable from '../components/BudgetTable.vue'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const error = ref(false)
const formData = reactive({
    "city": "",
    "budget": null,
    "days": null
})

const tripData = ref({})
/*
const tripData = {
    "success": true,
    "city": "北京",
    "days": 2,
    "totalBudget": 5000,
    "dailyItinerary": [
        {
            "day": 1,
            "date": "第1天",
            "morning": {
                "spot": "故宫博物院",
                "duration": "3-4小时",
                "ticket": "60元（旺季）/40元（淡季）",
                "transportation": "地铁1号线天安门东站A口出，步行前往。",
                "description": "故宫旧称紫禁城，是中国明清两代的皇家宫殿，位于北京中轴线的中心。它是世界上现存规模最大、保存最为完整的木质结构古建筑之一。建议从午门进入，沿中轴线参观三大殿，再游览东西六宫，最后从神武门离开。这里承载了六百年的历史沧桑，是感受皇权威严与古代建筑艺术的最佳去处。"
            },
            "afternoon": {
                "spot": "景山公园 & 什刹海",
                "duration": "3-4小时",
                "transportation": "从故宫神武门出来对面即是景山公园；游览完景山后可步行或打车前往什刹海。",
                "description": "景山公园位于故宫北面，登上万春亭可俯瞰故宫全景，是北京城最好的观景台之一。随后前往什刹海景区，这里保留了老北京的胡同风貌，可以乘坐人力三轮车游胡同，参观恭王府（和珅故居），感受‘西湖春，秦淮夏，洞庭秋’的美丽风光，体验京城水乡的独特韵味。"
            },
            "evening": {
                "spot": "全聚德烤鸭店（前门店） & 前门大街",
                "duration": "2-3小时",
                "ticket": "人均200元左右",
                "transportation": "从什刹海打车或地铁前往前门站。",
                "description": "来到北京怎能不吃烤鸭？前门全聚德是老字号总店，品尝正宗北京烤鸭，皮脆肉嫩。用餐后漫步前门大街，这里保留了民国时期的建筑风格，有大栅栏商业街，可以购买稻香村糕点等特产，感受北京夜晚的繁华与古韵。"
            }
        },
        {
            "day": 2,
            "date": "第2天",
            "morning": {
                "spot": "慕田峪长城",
                "duration": "4小时",
                "transportation": "建议包车前往（约400元往返）或乘坐慕巴士专线；距离市区约70公里。",
                "description": "相比人山人海的八达岭长城，慕田峪长城景色更加秀丽，游客相对较少，游览体验更佳。这里植被覆盖率高，城墙修缮完好，著名的‘涂鸦墙’也在此处。建议乘坐缆车上下，节省体力，在长城上感受‘不到长城非好汉’的豪迈气概。"
            },
            "afternoon": {
                "spot": "鸟巢（国家体育场） & 水立方",
                "duration": "2小时",
                "transportation": "从长城返回市区，地铁8号线奥体中心站。",
                "description": "返回市区后，前往奥林匹克公园。外观2008年北京奥运会的主场馆——鸟巢和水立方。这里现代建筑风格独特，气势恢宏，是北京现代都市风貌的代表。可以在广场上拍照留念，感受奥运精神。"
            },
            "evening": {
                "spot": "簋街（美食街）",
                "duration": "2小时",
                "ticket": "人均150元",
                "transportation": "地铁5号线北新桥站。",
                "description": "簋街是北京著名的美食街，号称‘夜猫子’的圣地。这里灯火通明，以麻辣小龙虾、馋嘴蛙、烤鱼等特色美食闻名。在这里可以体验北京热闹的夜生活和地道的江湖菜，结束愉快的两天旅程。"
            }
        }
    ],
    "budgetBreakdown": {
        "accommodation": 1200,
        "food": 1200,
        "transportation": 800,
        "tickets": 400,
        "other": 1400
    },
    "tips": [
        "故宫门票非常紧张，务必提前7天在官网或微信小程序预约，通常下午5点放票。",
        "游览长城建议穿舒适的运动鞋，台阶较陡，注意安全。",
        "北京早晚高峰交通拥堵严重，建议尽量选择地铁出行。",
        "住宿建议选择前门、王府井或地铁2号线沿线，交通便利且靠近市中心景点。"
    ],
    "warnings": [
        "北京气候干燥，请随身携带水杯，注意补水保湿。",
        "天安门广场进入需要安检，请携带好身份证，尽量不要携带大包，以减少安检排队时间。",
        "景区周边会有很多低价一日游或推销的人员，请勿轻信，以免受骗。"
    ]
}
    */
const activeDays = ref([])

const goBack = () => {
    router.back()
}

const geToChat = () => {
    router.push({ name: 'Chat' })
}

const fetchRecommend = async () => {
    loading.value = true
    error.value = false

    try {
        const res = await recommendTravel({
            city: formData.city,
            budget: parseInt(formData.budget),
            days: parseInt(formData.days)
        })
        console.log('Recommend result:', res)
        tripData.value = res.data
    } catch (err) {
        console.error('Recommend request failed:', err)
        error.value = true
    } finally {
        loading.value = false
    }

    console.log(tripData.value)
    //loading.value = false
}


onMounted(() => {
    console.log(route.query)
    formData.city = route.query.city
    formData.budget = route.query.budget
    formData.days = route.query.days
    fetchRecommend()
})

</script>

<style scoped>
.page-content{
    padding: 16px;
}
.page-container {
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 70px;
}
.card {
    background-color: #fff;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.section-title {
    font-size: 18px;
    font-weight: 600;
    color: #323233;
    margin-bottom: 12px;
}
.overview-card {
  margin-bottom: 16px;
}

.trip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.trip-header h2 {
  font-size: 20px;
  color: #323233;
  margin: 0;
}

.trip-budget {
  font-size: 16px;
  color: #ee0a24;
  font-weight: 600;
}

.trip-collapse {
  margin-bottom: 16px;
}

.day-schedule {
  padding: 8px 0;
}

.schedule-section {
  margin-bottom: 16px;
}

.schedule-section:last-child {
  margin-bottom: 0;
}

.section-label {
  font-size: 14px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
  margin-bottom: 8px;
}

.section-label.morning {
  background: #fff7e6;
  color: #fa8c16;
}

.section-label.afternoon {
  background: #e6f7ff;
  color: #1890ff;
}

.section-label.evening {
  background: #f6ffed;
  color: #52c41a;
}

.budget-card,
.tips-card,
.warnings-card {
  margin-bottom: 16px;
}

.tips-list,
.warnings-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li,
.warnings-list li {
  padding: 8px 0;
  color: #666;
  font-size: 14px;
  border-bottom: 1px solid #f5f5f5;
}

.tips-list li:last-child,
.warnings-list li:last-child {
  border-bottom: none;
}

.detail-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  max-width: 750px;
  margin: 0 auto;
}

.error-card {
  text-align: center;
  padding: 40px 16px;
}
</style>
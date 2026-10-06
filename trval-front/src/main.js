import { createApp } from 'vue'
//import './style.css'
import App from './App.vue'
import {
    Button,
    Card,
    Tabbar,
    TabbarItem,
    Icon,
    NavBar,
    NoticeBar,
    Field,
    Popup,
    Picker,
    Grid,
    GridItem,
    Tag,
    Loading,
    Empty,
    Collapse,
    CollapseItem,
    CellGroup,
    Cell,
    Image as VanImage
} from 'vant'
import 'vant/lib/index.css'
import router from './router'

const app = createApp(App)
app.use(Button)
app.use(Card)
app.use(Tabbar)
app.use(TabbarItem)
app.use(Icon)
app.use(NavBar)
app.use(NoticeBar)
app.use(Field)
app.use(Popup)
app.use(Picker)
app.use(Grid)
app.use(GridItem)
app.use(Tag)
app.use(Loading)
app.use(Empty)
app.use(Collapse)
app.use(CollapseItem)
app.use(CellGroup)
app.use(Cell)
app.use(VanImage)
app.use(router)
app.mount('#app')

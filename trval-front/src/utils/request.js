import axios from 'axios'

const request = axios.create({
  //baseURL: 'http://159.75.169.224:1236',
  baseURL: 'http://127.0.0.1:3200',
  //baseURL: '/',
  timeout: 60000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

export async function fetchStream(url,data,onChunk,onComplete,onError) {
  //终止请求控制器
  const controller = new AbortController()

  //async+await同步
  //const response = await fetch(`/api/travel/${url}`, {
  const response = await fetch(`http://127.0.0.1:3200/api/travel/${url}`, {
    method: 'POST',
    body: JSON.stringify(data),
    headers: {
      'Content-Type': 'application/json'
    },
    signal:controller.signal
  })

  //创建响应体的可读流读取器
  const reader = response.body.getReader()
  //将二进制数据解码为字符串
  const decoder = new TextDecoder()

  let result = ''

  while(true){
    const {done,value} = await reader.read()
    if(done){
      break
    }

    const chunk = decoder.decode(value,{stream:true})
    console.log(chunk)

    const lines = chunk.split('\n').filter(line => line.trim())
    for(const line of lines){
      if(line.startsWith('data:')){
        const jsonStr = line.substring(5)

        try{
          if(jsonStr){
            const jsanData = JSON.parse(jsonStr)
            if(jsanData.type==='chunk'){
              //分片数据
              onChunk(jsanData.content)
              result += jsanData.content
            }
            else if(jsanData.done === true){
              //完成数据
              onComplete(result)
            }
            else if(jsanData.error){
              //错误数据
              onError(jsanData.error)
            }
          }
        }
        catch(error){
            onError('流式数据解析异常')
        }
      }
    }
  }
  return controller.abort()
}

export default request
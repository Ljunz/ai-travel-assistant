import request from './request'

export const recommendTravel = (params) => {
  return request({
    url: '/api/travel/recommend',
    method: 'post',
    data: params
  })
}
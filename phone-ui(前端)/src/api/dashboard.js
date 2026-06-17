import request from '@/utils/request'

// 获取首页统计数据
export function getDashboardStats() {
  return request({
    url: '/repair/dashboard/stats',
    method: 'get'
  })
}

// 获取维修订单趋势数据
export function getRepairOrderTrend() {
  return request({
    url: '/repair/dashboard/trend',
    method: 'get'
  })
}

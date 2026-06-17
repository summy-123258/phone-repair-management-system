import request from '@/utils/request'

export function listRepairOrder(query) {
  return request({
    url: '/repair/repair-order/page',
    method: 'get',
    params: query
  })
}

export function getRepairOrder(id) {
  return request({
    url: '/repair/repair-order/get?id=' + id,
    method: 'get'
  })
}

export function addRepairOrder(data) {
  return request({
    url: '/repair/repair-order/create',
    method: 'post',
    data: data
  })
}

export function updateRepairOrder(data) {
  return request({
    url: '/repair/repair-order/update',
    method: 'put',
    data: data
  })
}

export function delRepairOrder(id) {
  return request({
    url: '/repair/repair-order/delete?id=' + id,
    method: 'delete'
  })
}

export function updateRepairOrderStatus(id, status, statusText) {
  return request({
    url: '/repair/repair-order/update-status?id=' + id + '&status=' + status + '&statusText=' + statusText,
    method: 'put'
  })
}

export function listRepairProgress(orderId) {
  return request({
    url: '/repair/repair-progress/list?orderId=' + orderId,
    method: 'get'
  })
}

export function addRepairProgress(data) {
  return request({
    url: '/repair/repair-progress/create',
    method: 'post',
    data: data
  })
}

export function updateRepairOrderCost(id, cost) {
  return request({
    url: '/repair/repair-order/update-cost?id=' + id + '&cost=' + cost,
    method: 'put'
  })
}

export function exportRepairOrder(query) {
  return request({
    url: '/repair/repair-order/export-excel',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

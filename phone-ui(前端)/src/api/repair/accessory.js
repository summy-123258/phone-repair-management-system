import request from '@/utils/request'

// 配件管理相关API
export function listAccessory() {
  return request({
    url: '/repair/accessory/list',
    method: 'get'
  })
}

export function listAccessoryPage(params) {
  return request({
    url: '/repair/accessory/list/page',
    method: 'get',
    params: params
  })
}

export function listAccessoryByKeyword(keyword) {
  return request({
    url: '/repair/accessory/list-by-keyword?keyword=' + keyword,
    method: 'get'
  })
}

export function listAccessoryByKeywordPage(params) {
  return request({
    url: '/repair/accessory/list-by-keyword/page',
    method: 'get',
    params: params
  })
}

export function replenishAccessory(data) {
  return request({
    url: '/repair/accessory/replenish',
    method: 'post',
    params: data
  })
}

export function getAccessory(id) {
  return request({
    url: '/repair/accessory/get?id=' + id,
    method: 'get'
  })
}

export function addAccessory(data) {
  return request({
    url: '/repair/accessory/create',
    method: 'post',
    data: data
  })
}

export function updateAccessory(data) {
  return request({
    url: '/repair/accessory/update',
    method: 'put',
    data: data
  })
}

export function delAccessory(id) {
  return request({
    url: '/repair/accessory/delete?id=' + id,
    method: 'delete'
  })
}

// 配件流水相关API
export function createAccessoryUse(data) {
  return request({
    url: '/repair/accessory/flow/use',
    method: 'post',
    data: data
  })
}

export function createAccessoryReturn(data) {
  return request({
    url: '/repair/accessory/flow/return',
    method: 'post',
    data: data
  })
}

export function listAccessoryFlowByRepairOrder(repairOrderNo) {
  return request({
    url: '/repair/accessory/flow/list-by-repair-order?repairOrderNo=' + repairOrderNo,
    method: 'get'
  })
}

export function listAccessoryFlowByAccessory(accessoryId) {
  return request({
    url: '/repair/accessory/flow/list-by-accessory?accessoryId=' + accessoryId,
    method: 'get'
  })
}

export function getAvailableReturnQuantity(repairOrderNo, accessoryId) {
  return request({
    url: '/repair/accessory/flow/available-return-quantity?repairOrderNo=' + repairOrderNo + '&accessoryId=' + accessoryId,
    method: 'get'
  })
}

export function listAllAccessoryFlow() {
  return request({
    url: '/repair/accessory/flow/list',
    method: 'get'
  })
}

// 配件预警相关API
export function listAccessoryWarning() {
  return request({
    url: '/repair/accessory/warning/list',
    method: 'get'
  })
}

export function listAccessoryWarningByStatus(status) {
  return request({
    url: '/repair/accessory/warning/list-by-status?status=' + status,
    method: 'get'
  })
}

export function updateWarningStatus(id, status) {
  return request({
    url: '/repair/accessory/warning/update-status?id=' + id + '&status=' + status,
    method: 'put'
  })
}

export function checkAllAccessoryWarnings() {
  return request({
    url: '/repair/accessory/warning/check-all',
    method: 'post'
  })
}

import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getAccessToken } from '@/utils/auth'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/social-login',  '/auth-redirect', '/bind', '/register', '/oauthLogin/gitee']

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (getAccessToken()) {
    to.meta.title && store.dispatch('settings/setTitle', to.meta.title)
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      if (store.getters.roles.length === 0) {
        isRelogin.show = true
        store.dispatch('dict/loadDictDatas')
        store.dispatch('GetInfo').then(userInfo => {
          isRelogin.show = false

          const roles = store.getters.roles
          const hideHomeRoute = roles.includes('repair') || roles.includes('super_admin')

          if (hideHomeRoute && (to.path === '/' || to.path === '/index')) {
            store.dispatch('GenerateRoutes', {menus: userInfo.menus, roles: roles}).then(accessRoutes => {
              router.addRoutes(accessRoutes)
              next({ path: '/repair/repairOrder', replace: true })
            })
            return
          }

          store.dispatch('GenerateRoutes', {menus: userInfo.menus, roles: roles}).then(accessRoutes => {
            router.addRoutes(accessRoutes)
            next({ ...to, replace: true })
          })
        }).catch(err => {
          store.dispatch('LogOut').then(() => {
            Message.error(err)
            next({ path: '/' })
          })
        })
      } else {
        const roles = store.getters.roles
        const hideHomeRoute = roles.includes('repair') || roles.includes('super_admin')

        if (hideHomeRoute && (to.path === '/' || to.path === '/index')) {
          next('/repair/repairOrder')
          return
        }
        next()
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      const redirect = encodeURIComponent(to.fullPath)
      next(`/login?redirect=${redirect}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

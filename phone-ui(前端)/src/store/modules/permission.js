import {constantRoutes} from '@/router'
import Layout from '@/layout/index'
import ParentView from '@/components/ParentView';
import {toCamelCase} from "@/utils";

const permission = {
  state: {
    routes: [],
    addRoutes: [],
    sidebarRouters: [],
    topbarRouters: [],
  },
  mutations: {
    SET_ROUTES: (state, routes) => {
      state.addRoutes = routes
      state.routes = constantRoutes.concat(routes)
    },
    SET_DEFAULT_ROUTES: (state, routes) => {
      state.defaultRoutes = constantRoutes.concat(routes)
    },
    SET_TOPBAR_ROUTES: (state, routes) => {
      state.topbarRouters = routes
    },
    SET_SIDEBAR_ROUTERS: (state, routes) => {
      state.sidebarRouters = routes
    },
  },
  actions: {
    GenerateRoutes({commit}, {menus, roles}) {
      return new Promise(resolve => {
        const sdata = JSON.parse(JSON.stringify(menus))
        const rdata = JSON.parse(JSON.stringify(menus))
        const sidebarRoutes = filterAsyncRouter(sdata)
        const rewriteRoutes = filterAsyncRouter(rdata, false, true)
        rewriteRoutes.push({path: '*', redirect: '/404', hidden: true})

        let filteredConstantRoutes = JSON.parse(JSON.stringify(constantRoutes))

        const hideHomeRoute = roles && (roles.includes('repair') || roles.includes('super_admin'))
        if (hideHomeRoute) {
          filteredConstantRoutes = filteredConstantRoutes.filter(route => {
            if (route.redirect === 'index' && route.children) {
              return !route.children.some(child => child.path === 'index')
            }
            return true
          })
        }

        commit('SET_ROUTES', rewriteRoutes)
        commit('SET_SIDEBAR_ROUTERS', filteredConstantRoutes.concat(sidebarRoutes))
        commit('SET_DEFAULT_ROUTES', sidebarRoutes)
        commit('SET_TOPBAR_ROUTES', sidebarRoutes)
        resolve(rewriteRoutes)
      })
    }
  }
}

function filterAsyncRouter(asyncRouterMap, lastRouter = false, type = false) {
  return asyncRouterMap.filter(route => {
    route.meta = {
      title: route.name,
      icon: route.icon,
      noCache: !route.keepAlive,
    }
    route.hidden = !route.visible
    if (route.componentName && route.componentName.length > 0) {
      route.name = route.componentName
    } else {
      route.name = toCamelCase(route.path, true)
      if (route.path.indexOf("/") !== -1) {
        const pathArr = route.path.split("/");
        route.name = toCamelCase(pathArr[pathArr.length - 1], true)
      }
    }
    if (route.children) {
      if (route.parentId === 0) {
        route.component = Layout
      } else {
        route.component = ParentView
      }
    } else {
      route.component = loadView(route.component)
    }

    if (type && route.children) {
      route.children = filterChildren(route.children)
    }
    if (route.children != null && route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children, route, type)
      route.alwaysShow = route.alwaysShow !== undefined ? route.alwaysShow  : true
    } else {
      delete route['children']
      delete route['alwaysShow']
    }
    return true
  })
}

function filterChildren(childrenMap, lastRouter = false) {
  let children = [];
  childrenMap.forEach((el, index) => {
    if (el.children && el.children.length) {
      if (!el.component && !lastRouter) {
        el.children.forEach(c => {
          c.path = el.path + '/' + c.path
          if (c.children && c.children.length) {
            children = children.concat(filterChildren(c.children, c))
            return
          }
          children.push(c)
        })
        return
      }
    }
    if (lastRouter) {
      el.path = lastRouter.path + '/' + el.path
    }
    children = children.concat(el)
  })
  return children
}

export const loadView = (view) => {
  return (resolve) => require([`@/views/${view}`], resolve)
}

export default permission

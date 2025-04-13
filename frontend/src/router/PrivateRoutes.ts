const MainRoutes = {
  path: '/agents',
  meta: {
    requiresAuth: true
  },
  redirect: '/agents',
  component: () => import('@/layouts/dashboard/PrivateLayout.vue'),
  children: [
    
    {
      name: 'Agents',
      path: '/agents',
      component: () => import('@/views/apps/agents/AgentListPage.vue')
    },
    // {
    //   name: 'Typography',
    //   path: '/typography',
    //   component: () => import('@/views/typography/TypographyPage.vue')
    // },
    // {
    //   name: 'Colors',
    //   path: '/colors',
    //   component: () => import('@/views/colors/ColorPage.vue')
    // },
    // {
    //   name: 'Shadow',
    //   path: '/shadow',
    //   component: () => import('@/views/shadows/ShadowPage.vue')
    // },
    // {
    //   name: 'Color',
    //   path: '/icon/ant',
    //   component: () => import('@/views/icons/AntDesignIcons.vue')
    // },
    // {
    //   name: 'text-diff',
    //   path: '/text-diff',
    //   component: () => import('@/views/apps/text/TextDiff.vue')
    // },
    // {
    //   name: 'token',
    //   path: '/token',
    //   component: () => import('@/views/authentication/auth/AuthToken.vue')
    // }
  ]
};

export default MainRoutes;

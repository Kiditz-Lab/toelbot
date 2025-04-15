const MainRoutes = {
  path: '/main',
  meta: {
    requiresAuth: true
  },
  redirect: '/main',
  component: () => import('@/layouts/dashboard/DashboardLayout.vue'),
  children: [
    
    {
      name: 'Integration',
      path: '/:id/integration',
      component: () => import('@/views/apps/integration/IntegrationPage.vue')
    },
    {
      name: 'Settings',
      path: '/:id/settings',
      component: () => import('@/views/apps/settings/SettingsPage.vue')
    },
    {
      name: 'Typography',
      path: '/:id/typography',
      component: () => import('@/views/typography/TypographyPage.vue')
    },
    {
      name: 'Chats',
      path: '/:id/chats',
      component: () => import('@/views/apps/chats/ChatPage.vue')
    },
    {
      name: 'ChatHistory',
      path: '/:id/chat-history',
      component: () => import('@/views/apps/chats/ChatHistoryPage.vue')
    },
    {
      name: 'TrainingFile',
      path: '/:id/training-file',
      component: () => import('@/views/apps/training-data/TrainingFilePage.vue')
    },
    {
      name: 'TrainingText',
      path: '/:id/training-text',
      component: () => import('@/views/apps/training-data/TrainingTextPage.vue')
    },
    {
      name: 'TrainingWebsite',
      path: '/:id/training-website',
      component: () => import('@/views/apps/training-data/TrainingWebsitePage.vue')
    },
    {
      name: 'Tools',
      path: '/:id/tools',
      component: () => import('@/views/apps/tools/ToolsPage.vue')
    },
    {
      name: 'ToolDetail',
      path: '/:id/tool/detail',
      component: () => import('@/views/apps/tools/ToolDetailPage.vue')
    },
    // {
    //   name: 'Colors',
    //   path: '/:id/colors',
    //   component: () => import('@/views/colors/ColorPage.vue')
    // },
    // {
    //   name: 'Shadow',
    //   path: '/:id/shadow',
    //   component: () => import('@/views/shadows/ShadowPage.vue')
    // },
    // {
    //   name: 'Color',
    //   path: '/:id/icon/ant',
    //   component: () => import('@/views/icons/AntDesignIcons.vue')
    // },
    // {
    //   name: 'text-diff',
    //   path: '/:id/text-diff',
    //   component: () => import('@/views/apps/text/TextDiff.vue')
    // },
    {
      name: 'token',
      path: '/:id/token',
      component: () => import('@/views/authentication/auth/AuthToken.vue')
    }
  ]
};

export default MainRoutes;

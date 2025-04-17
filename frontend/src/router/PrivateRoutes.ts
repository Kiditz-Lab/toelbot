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
   
  ]
};

export default MainRoutes;

import { useApi } from '@/composables/useApi';
import type { FacebookPage } from '@/types/facebook';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
// import { useRoute } from 'vue-router';
import { useSnackbarStore } from '../snackbarStore';
import { useAgentStore } from './agentStore';

export const useFacbookStore = defineStore(
  'facebookStore',
  () => {
    const pages = ref<FacebookPage[]>([]);
    const loading = computed(() => api.loading);
    const api = useApi();
    const agentStore = useAgentStore()
    const { showSnackbar } = useSnackbarStore();
    const connectFacebook = () => {
      const clientId = import.meta.env.VITE_FACEBOOK_APP_ID;
      const baseUrl = import.meta.env.VITE_BASE_URL;

      const scope = 'pages_show_list,pages_messaging,public_profile';
      const redirectUri = `${baseUrl}/facebook/callback`;
      const fbAuthUrl = `https://www.facebook.com/v18.0/dialog/oauth?client_id=${clientId}&redirect_uri=${encodeURIComponent(redirectUri)}&scope=${scope}&response_type=code`;
      window.open(fbAuthUrl, '_blank', 'width=500,height=600');
    };
    const setPages = (newPages: FacebookPage[]) => {
      pages.value = newPages;
    };
    const assignPage = async (page: FacebookPage) => {
      try {
        page.loading = true;
        page.agentId = agentStore.agent!.id as string;
        await api.post('/save-page', page);
        await agentStore.fetchAgent();
        showSnackbar('Your page are assigned now', 'success', 3000, 'Success');
      } catch (error) {
        showSnackbar(error as string, 'error', 3000, 'Error');
      }finally{
        page.loading = false;
      }
    };
    return {
      connectFacebook,
      pages,
      setPages,
      assignPage,
      loading,
    };
  },
  { persist: true }
);

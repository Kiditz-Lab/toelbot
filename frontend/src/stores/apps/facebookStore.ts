import { useApi } from '@/composables/useApi';
import type { FacebookPage } from '@/types/facebook';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
// import { useRoute } from 'vue-router';
import { useSnackbarStore } from '../snackbarStore';
// import { useAgentStore } from './agentStore';
import { useRoute } from 'vue-router';

export const useFacbookStore = defineStore(
  'facebookStore',
  () => {
    const pages = ref<FacebookPage[]>([]);
    const loading = computed(() => api.loading);
    const api = useApi();

    const { showSnackbar } = useSnackbarStore();
    const route = useRoute();
    
    const fetchByAgent = async () => {
      const id = route.params.id as string;
      const res = await api.get<FacebookPage[]>(`/facebook-pages/${id}`);
      pages.value = res;
    };
    const connectFacebook = () => {
      const businessId = route.params.id as string;
      const state = businessId;

      const clientId = import.meta.env.VITE_FACEBOOK_APP_ID;
      const baseUrl = import.meta.env.VITE_BASE_URL;

      const scope = 'pages_show_list,pages_read_engagement,pages_messaging,public_profile,pages_manage_metadata';
      const redirectUri = `${baseUrl}/facebook/callback`;
      const fbAuthUrl =
        `https://www.facebook.com/v22.0/dialog/oauth` +
        `?client_id=${clientId}` +
        `&redirect_uri=${encodeURIComponent(redirectUri)}` +
        `&scope=${scope}` +
        `&response_type=code` +
        `&display=page` +
        `&state=${state}`;
      console.log(fbAuthUrl);
      window.open(fbAuthUrl, '_blank', 'width=500,height=600');
    };
    const setPages = (newPages: FacebookPage[]) => {
      pages.value = newPages;
    };

    const assignPage = async (page: FacebookPage) => {
      try {
        page.loading = true;
        const id = route.params.id as string;
        page.agentId = id;
        const res = await api.post<FacebookPage>('/facebook/subscribe-page', page);
        const index = pages.value.findIndex((p) => p.pageId === res.pageId);
        if (index !== -1) {
          pages.value[index] = { ...res, loading: false };
        }

        showSnackbar('Your page are subscribed', 'success', 3000, 'Success');
      } catch (error) {
        showSnackbar(error as string, 'error', 3000, 'Error');
      } finally {
        page.loading = false;
      }
    };
    const unassignPage = async (page: FacebookPage) => {
      try {
        page.loading = true;
        const id = route.params.id as string;
        page.agentId = id;
        const res = await api.del<FacebookPage>(`/facebook/unsubscribe-page/${page.pageId}`);
        const index = pages.value.findIndex((p) => p.pageId === res.pageId);
        if (index !== -1) {
          pages.value[index] = { ...res, loading: false };
        }

        showSnackbar(`Your ${page.name} are unsubscribed`, 'success', 3000, 'Success');
      } catch (error) {
        console.log(error);
        showSnackbar('Something went wrong, please try again.', 'error', 3000, 'Error');
      } finally {
        page.loading = false;
      }
    };
    return {
      connectFacebook,
      pages,
      setPages,
      assignPage,
      unassignPage,
      loading,
      fetchByAgent
    };
  },
  { persist: true }
);

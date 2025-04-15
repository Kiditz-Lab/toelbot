import type { FacebookPage } from '@/types/facebook';
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { useRoute } from 'vue-router';

export const useFacbookStore = defineStore(
  'facebookStore',
  () => {
    const pages = ref<FacebookPage[]>([]);
    const route = useRoute();

    const connectFacebook = () => {
      const clientId = import.meta.env.VITE_FACEBOOK_APP_ID;
      const baseUrl = import.meta.env.VITE_BASE_URL;
      const id = route.params.id;
      const scope = 'pages_show_list,pages_messaging,public_profile';
      const redirectUri = `${baseUrl}/facebook/callback/${id}`;
      const fbAuthUrl = `https://www.facebook.com/v18.0/dialog/oauth?client_id=${clientId}&redirect_uri=${encodeURIComponent(redirectUri)}&scope=${scope}&response_type=code`;
      window.open(fbAuthUrl, '_blank', 'width=500,height=600');
    };
    const setPages = (newPages: FacebookPage[]) => {
      pages.value = newPages;
    };
    return {
      connectFacebook,
      pages,
      setPages
    };
  },
  { persist: true }
);

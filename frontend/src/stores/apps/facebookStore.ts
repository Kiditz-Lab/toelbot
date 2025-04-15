import { defineStore } from 'pinia';

export const useFacbookStore = defineStore(
  'facebookStore',
  () => {
    const connectFacebook = () => {
      const clientId = import.meta.env.VITE_FACEBOOK_APP_ID;
      const baseUrl = import.meta.env.VITE_BASE_URL;
      const  scope = 'pages_show_list,pages_messaging,public_profile'
      const redirectUri = `${baseUrl}/facebook/callback`
      const fbAuthUrl = `https://www.facebook.com/v18.0/dialog/oauth?client_id=${clientId}&redirect_uri=${encodeURIComponent(redirectUri)}&scope=${scope}&response_type=code`
      window.open(fbAuthUrl, '_blank', 'width=600,height=700')
    };

    return {
      connectFacebook
    };
  },
  { persist: true }
);

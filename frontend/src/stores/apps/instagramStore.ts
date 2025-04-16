import { defineStore } from 'pinia';

export const useInstagramStore = defineStore('instagramStore', () => {
  const connectInstagram = () => {
    const clientId = import.meta.env.VITE_INSTAGRAM_APP_ID; // Same as Facebook
    const baseUrl = import.meta.env.VITE_BASE_URL;

    const scope = 'instagram_business_basic,instagram_business_manage_messages';
    const redirectUri = `${baseUrl}/instagram/callback`;

    const igAuthUrl = `https://www.instagram.com/oauth/authorize?enable_fb_login=0&force_authentication=1&client_id=${clientId}&redirect_uri=${encodeURIComponent(
      redirectUri
    )}&scope=${scope}&response_type=code`;
    console.log(igAuthUrl);
    window.open(igAuthUrl, '_blank', 'width=500,height=600');
  };

  return { connectInstagram };
});

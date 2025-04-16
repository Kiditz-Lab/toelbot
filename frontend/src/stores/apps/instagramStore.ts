import { defineStore } from 'pinia';

export const useInstagramStore = defineStore('instagramStore', () => {
  const connectInstagram = () => {
    const clientId = import.meta.env.VITE_INSTAGRAM_APP_ID; // Same as Facebook
    const baseUrl = import.meta.env.VITE_BASE_URL;

    const scope = 'instagram_basic,instagram_manage_messages,instagram_manage_comments,instagram_content_publish,instagram_manage_insights';
    const redirectUri = `${baseUrl}/instagram/callback`;

    const igAuthUrl = `https://www.instagram.com/oauth/authorize?enable_fb_login=0&force_authentication=1&client_id=${clientId}&redirect_uri=${encodeURIComponent(
      redirectUri
    )}&scope=${scope}&response_type=code`;

    window.open(igAuthUrl, '_blank', 'width=500,height=600');
  };

  return { connectInstagram };
});

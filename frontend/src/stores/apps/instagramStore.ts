import { defineStore } from 'pinia';

export const useInstagramStore = defineStore('instagramStore', () => {
  const connectInstagram = () => {
    const clientId = import.meta.env.VITE_FACEBOOK_APP_ID; // Same as Facebook
    const baseUrl = import.meta.env.VITE_BASE_URL;

    const scope = 'pages_show_list,pages_read_engagement,pages_messaging,instagram_basic,instagram_manage_messages';
    const redirectUri = `${baseUrl}/instagram/callback`;

    const igAuthUrl = `https://www.facebook.com/v22.0/dialog/oauth?client_id=${clientId}&redirect_uri=${encodeURIComponent(
      redirectUri
    )}&scope=${scope}&response_type=code`;

    window.open(igAuthUrl, '_blank', 'width=500,height=600');
  };

  return { connectInstagram };
});

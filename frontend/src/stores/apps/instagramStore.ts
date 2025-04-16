import { useApi } from '@/composables/useApi';
import type { InstagramAccount } from '@/types/instagram';
import { defineStore } from 'pinia';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

export const useInstagramStore = defineStore('instagramStore', () => {
  const account = ref<InstagramAccount | undefined>();
  const route = useRoute();
  const api = useApi();
  const fetchByAgent = async () => {
    const id = route.params.id as string;
    const res = await api.get<InstagramAccount[]>(`/instagram/accounts/${id}`);
    account.value = res[0];
  };
  onMounted(fetchByAgent);

  const connectInstagram = () => {
    const clientId = import.meta.env.VITE_INSTAGRAM_APP_ID; // Same as Facebook
    const baseUrl = import.meta.env.VITE_BASE_URL;
    const state = route.params.id;
    const scope = 'instagram_business_basic,instagram_business_manage_messages';
    const redirectUri = `${baseUrl}/instagram/callback`;

    const igAuthUrl = `https://www.instagram.com/oauth/authorize?enable_fb_login=0&force_authentication=1&client_id=${clientId}&redirect_uri=${encodeURIComponent(
      redirectUri
    )}&scope=${scope}&response_type=code&state=${state}`;

    window.open(igAuthUrl, '_blank', 'width=500,height=600');
  };
  const setAccount = (acc: InstagramAccount) => {
    account.value = acc;
  };

  return { connectInstagram, account, setAccount };
});

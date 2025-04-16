import { useApi } from '@/composables/useApi';
import type { InstagramAccount } from '@/types/instagram';
import { defineStore } from 'pinia';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useSnackbarStore } from '@/stores/snackbarStore';
const { showSnackbar } = useSnackbarStore();

export const useInstagramStore = defineStore('instagramStore', () => {
  const account = ref<InstagramAccount>({} as InstagramAccount);
  const route = useRoute();
  const api = useApi();
  const fetchByAgent = async () => {
    const id = route.params.id as string;
    const res = await api.get<InstagramAccount[]>(`/instagram/accounts/${id}`);
    account.value = res[0];
  };
  onMounted(fetchByAgent);
  const subscribe = async (acc: InstagramAccount) => {
    try {
      account.value.loading = true;
      const res = await api.post<InstagramAccount>(`/instagram/subscribe/${acc.userId}`, acc.userId);
      console.log(res);
      account.value = res;
      showSnackbar('Subscripation success', 'success', 3000, 'Success');
    } catch (error) {
      showSnackbar(error as string, 'error', 3000, 'Error');
    } finally {
      account.value.loading = false;
    }
  };
  const unsubscribe = async (acc: InstagramAccount) => {
    try {
      account.value.loading = true;
      const res = await api.post<InstagramAccount>(`/instagram/unsubscribe/${acc.userId}`, {});
      console.log(res);
      account.value = res;

      showSnackbar('Unsubscribe success', 'success', 3000, 'Success');
    } catch (error) {
      showSnackbar(error as string, 'error', 3000, 'Error');
    } finally {
      account.value.loading = false;
    }
  };
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

  return { connectInstagram, account, setAccount, subscribe, unsubscribe };
});

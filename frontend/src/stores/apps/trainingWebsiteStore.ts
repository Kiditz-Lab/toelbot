import { useApi } from '@/composables/useApi';
import type { TrainingData } from '@/types/training_data';
import { Client, type IMessage } from '@stomp/stompjs';
import { defineStore } from 'pinia';
import SockJS from 'sockjs-client';
import { computed, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useSnackbarStore } from '../snackbarStore';
export const useTrainingWebsiteStore = defineStore(
  'trainingWebsiteStore',
  () => {
    const api = useApi();
    const selectedItems = ref([] as string[]);
    const { showSnackbar } = useSnackbarStore();
    const trainings = ref([] as TrainingData[]);
    const dialog = ref(false);
    const search = ref('');
    const loading = computed(() => api.loading);
    const urlRules = [
      (v: string) => !!v || 'URL is required', // Required field
      (v: string) => /^(https?:\/\/)?([\da-z.-]+)\.([a-z.]{2,6})([/\w .-]*)*\/?$/.test(v) || 'Enter a valid URL'
    ];
    const route = useRoute();
    const formRef = ref<InstanceType<typeof HTMLFormElement> | null>(null);
    const url = ref('');
    const totalItems = ref(0);
    const stompClient = ref<Client | null>(null);

    async function crawlWebsite() {
      const id = route.params.id as string;
      console.log('Crawl website');
      if (formRef.value?.validate()) {
        const response = await api.get<string[]>(`/crawl/${id}`, { url: url.value });
        console.log(response);
        showSnackbar('Crawling in progress. This might take a few moments.', 'blue-grey', 3000, 'Success');
      }
    }

    async function connect() {
      const baseURL = import.meta.env.VITE_WS_URL as string;
      const socket = new SockJS(baseURL);
      const agentId = route.params.id as string;
      if (!agentId) {
        console.error('Agent ID is required for WebSocket connection.');
        return;
      }
      stompClient.value = new Client({
        webSocketFactory: () => socket as WebSocket,
        debug: (msg) => console.log(msg),
        onConnect: () => {
          stompClient.value?.subscribe(`/topic/crawl-finish/${agentId}`, async (message: IMessage) => {
            try {
              console.log(message.body);
              await fetchTrainingWebsite();
              showSnackbar('Your website successfully crawled', 'info', 3000, 'Success');
            } catch (error) {
              console.error('Error parsing WebSocket message:', error);
            }
          });
          stompClient.value?.subscribe(`/topic/crawl-chunk/${agentId}`, async (message: IMessage) => {
            try {
              console.log(message.body);
              await fetchTrainingWebsite();
            } catch (error) {
              console.error('Error parsing WebSocket message:', error);
            }
          });
          stompClient.value?.subscribe(`/topic/training-delete/${agentId}`, async (message: IMessage) => {
            console.log(message.body);
            await fetchTrainingWebsite();
          });
          stompClient.value?.subscribe(`/topic/training/${agentId}`, (message: IMessage) => {
            try {
              const receivedData = JSON.parse(message.body) as TrainingData;
              const index = trainings.value.findIndex((t) => t.id === receivedData.id);
              if (index !== -1) {
                trainings.value[index] = receivedData;
              }
              // showSnackbar('Your data successfully trained', 'info', 3000, 'Success');
              console.log('Received message:', receivedData);
            } catch (error) {
              console.error('Error parsing WebSocket message:', error);
            }
          });
        },
        onDisconnect: () => {
          console.log('Disconnected from WebSocket');
        }
      });
      stompClient.value.activate();
    }

    const options = ref({
      page: 1,
      itemsPerPage: 20,
      sortBy: [{ key: 'url', order: 'asc' }]
    });

    async function fetchTrainingWebsite() {
      selectedItems.value = [];
      const id = route.params.id as string;
      // const response = await api.get<TrainingData[]>(`/training-data/${id}`, { type: 'LINK', sort: 'url,asc' });
      if (!id) return;

      try {
        const { page, itemsPerPage, sortBy } = options.value;

        // Construct multiple sort parameters
        const sortParams = sortBy.filter((sortItem) => sortItem.order).map((sortItem) => `${sortItem.key},${sortItem.order}`);

        // Build query params
        const params: Record<string, unknown> = {
          page: page - 1,
          size: itemsPerPage
        };

        if (sortParams.length > 0) {
          params.sort = sortParams;
        } else {
          params.sort = 'url,asc';
        }

        const response = await api.get<{ content: TrainingData[]; totalElements: number }>(`/training-data/${id}`, {
          type: 'LINK',
          search: search.value,
          ...params
        });

        trainings.value = response.content;
        totalItems.value = response.totalElements;
      } catch (error) {
        console.error('Error fetching websites:', error);
      }
    }

    async function deleteTrainingData(id: string) {
      await api.del(`/training-data/${id}`);
    }
    const disconnect = () => {
      if (stompClient.value) {
        stompClient.value.deactivate();
      }
    };
    async function trainWebsite() {
      await api.post(`/training-data/train-website`, selectedItems.value);
    }
    async function confirmDelete() {
      dialog.value = false;
      try {
        await api.del(`/training-data`, { ids: selectedItems.value });
        showSnackbar('Url deleted successfully', 'success', 3000, 'Success');
      } catch (err) {
        console.log(err);
        showSnackbar('Url deleted failed', 'error', 3000, 'Error');
      }
    }
    async function openDeleteDialog() {
      dialog.value = true;
    }

    watch(options, fetchTrainingWebsite, { deep: true });

    let searchTimeout: number;

    watch(search, () => {
      clearTimeout(searchTimeout);
      searchTimeout = setTimeout(() => {
        fetchTrainingWebsite();
      }, 500);
    });

    return {
      formRef,
      url,
      search,
      urlRules,
      crawlWebsite,
      loading,
      trainings,
      dialog,
      connect,
      disconnect,
      fetchTrainingWebsite,
      trainWebsite,
      deleteTrainingData,
      options,
      totalItems,
      selectedItems,
      openDeleteDialog,
      confirmDelete
    };
  },
  {
    persist: false
  }
);

import type { TrainingData } from '@/types/training_data';
import { defineStore } from 'pinia';
import { useApi } from '@/composables/useApi';
import { computed, ref } from 'vue';

import type { TrainingSize } from '@/types/training_size';
import SockJS from 'sockjs-client';

import { Client, type Message as IMessage } from '@stomp/stompjs';
import { useRoute } from 'vue-router';
import { useSnackbarStore } from '../snackbarStore';
const { showSnackbar } = useSnackbarStore();
const api = useApi();

export const useTrainingFileStore = defineStore(
  'trainingStore',
  () => {
    const route = useRoute();
    const stompClient = ref<Client | null>(null);
    const trainings = ref<TrainingData[]>([]);
    const size = ref<TrainingSize>({} as TrainingSize);
    const uploadProgress = computed(() => api.uploadProgress);
    const downloadProgress = computed(() => api.downloadProgress);
    const loading = ref(false);
    const totalItems = ref(0);
    const options = ref({
      page: 1,
      itemsPerPage: 20,
      sortBy: [{ key: 'url', order: 'asc' }]
    });

    async function fetchTrainingFile() {
      try {
        const id = route.params.id as string;
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
          type: 'FILE',
          ...params
        });
        trainings.value = response.content;
        totalItems.value = response.totalElements;
      } catch (error) {
        console.error('Error fetching training file:', error);
      }
    }

    async function fetchTrainingSize() {
      const id = route.params.id as string;
      const response = await api.get<TrainingSize>(`/training-data/${id}/size`);
      Object.assign(size.value, response);
    }

    async function trainFileAgent() {
      loading.value = true;
      const id = route.params.id as string;
      await api.post(`/training-data/${id}/train-file`);
      await fetchTrainingFile();
      loading.value = false;
    }
    async function deleteTrainingData(id: string) {
      await api.del(`/training-data/${id}`);
    }

    async function uploadFile(files: File[]) {
      const id = route.params.id as string;
      const form = new FormData();
      files.forEach((file) => {
        form.append('files', file);
      });

      await api.post<TrainingData[]>(`/training-data/${id}/upload`, form, { 'Content-Type': 'multipart/form-data' });
      await fetchTrainingFile();
      await fetchTrainingSize();
    }
    const connect = () => {
      console.log('Connecting to WebSocket...');
      const agentId = route.params.id as string;
      if (!agentId) {
        console.error('Agent ID is required for WebSocket connection.');
        return;
      }
      const baseURL = import.meta.env.VITE_WS_URL as string;
      const socket = new SockJS(baseURL);
      stompClient.value = new Client({
        webSocketFactory: () => socket as WebSocket,
        debug: (msg) => console.log(msg),
        onConnect: () => {
          console.log('Connected to WebSocket');
          stompClient.value?.subscribe(`/topic/training/${agentId}`, (message: IMessage) => {
            try {
              const receivedData = JSON.parse(message.body) as TrainingData;
              const index = trainings.value.findIndex((t) => t.id === receivedData.id);
              if (index !== -1) {
                trainings.value[index] = receivedData;
              }
              showSnackbar('Your data successfully trained', 'info', 3000, 'Success');
              console.log('Received message:', receivedData);
            } catch (error) {
              console.error('Error parsing WebSocket message:', error);
            }
          });
          stompClient.value?.subscribe(`/topic/training-delete/${agentId}`, async (message: IMessage) => {
            console.log(message.body);
            await fetchTrainingFile();
          });
        },
        onDisconnect: () => {
          console.log('Disconnected from WebSocket');
        }
      });

      stompClient.value.activate();
    };

    const fileChanges = (newFiles: File[]) => {
      uploadFile(newFiles);
    };

    const disconnect = () => {
      if (stompClient.value) {
        stompClient.value.deactivate();
      }
    };

    return {
      fileChanges,
      fetchTrainingFile,
      disconnect,
      connect,
      trainings,
      size,
      fetchTrainingSize,
      uploadProgress,
      downloadProgress,
      trainAgent: trainFileAgent,
      loading,
      deleteTrainingData
    };
  }
);

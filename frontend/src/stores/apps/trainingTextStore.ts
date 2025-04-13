import { useApi } from '@/composables/useApi';
import type { TrainingData } from '@/types/training_data';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';

export const useTrainingTextStore = defineStore('trainingTextStore', () => {
  const api = useApi();
  const loading = ref(false);
  const training = ref<TrainingData>({} as TrainingData);
  const text = ref('');
  const route = useRoute();
  const textSize = computed(() => new TextEncoder().encode(text.value).length);
  async function fetchTrainingText() {
    const id = route.params.id as string;
    const response = await api.get<TrainingData[]>(`/training-data/${id}`, { type: 'TEXT' });
    if (response.length > 0) {
      training.value = response[0];
      text.value = response[0].content;
    }
  }
  async function trainTextAgent() {
    loading.value = true;
    const id = route.params.id as string;
    await api.post(`/training-data/${id}/train-text`, { content: text.value });
    await fetchTrainingText();
    loading.value = false;
  }

  return { text, textSize, trainTextAgent, loading, fetchTrainingText, training };
}, {
  persist: false
});

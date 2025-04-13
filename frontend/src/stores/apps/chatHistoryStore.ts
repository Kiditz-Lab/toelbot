import { defineStore } from 'pinia';
import { ref, watch } from 'vue';
import { useApi } from '@/composables/useApi';
import { useRoute } from 'vue-router';
import type { ChatHistory } from '@/types/chat_history';

export const useChatHistoryStore = defineStore('chatHistoryStore', () => {
  const api = useApi();
  const route = useRoute();

  const agentId = ref(route.params.id);
  const histories = ref<ChatHistory[]>([]);
  const totalItems = ref(0);
  const loading = ref(false);

  const options = ref({
    page: 1,
    itemsPerPage: 10,
    sortBy: [{ key: 'createdAt', order: 'desc' }],
  });

  async function fetchHistories() {
    if (!agentId.value) return;

    loading.value = true;
    try {
      const { page, itemsPerPage, sortBy } = options.value;

      // Construct multiple sort parameters
      const sortParams = sortBy
        .filter((sortItem) => sortItem.order)
        .map((sortItem) => `${sortItem.key},${sortItem.order}`);

      // Build query params
      const params: Record<string, unknown> = {
        page: page - 1,
        size: itemsPerPage,
      };

      if (sortParams.length > 0) {
        params.sort = sortParams;
      } else {
        params.sort = 'createdAt,desc';
      }

      const response = await api.get<{ content: ChatHistory[]; totalElements: number }>(
        `/histories/${agentId.value}`,
        params
      );

      histories.value = response.content;
      totalItems.value = response.totalElements;
    } catch (error) {
      console.error('Error fetching chat histories:', error);
    } finally {
      loading.value = false;
    }
  }

  // Watch options for changes and fetch data
  watch(options, fetchHistories, { deep: true });

  watch(() => route.params.id, (newId) => {
    agentId.value = newId;
    fetchHistories();
  });

  return { histories, totalItems, loading, options, fetchHistories };
});

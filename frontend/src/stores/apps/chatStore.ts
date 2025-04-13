import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useApi } from '@/composables/useApi';
import { useRoute } from 'vue-router';
import { v4 as uuidv4 } from 'uuid';

import type { ChatMessage } from '@/types/chat';
import { useSnackbarStore } from '../snackbarStore';

export const useChatStore = defineStore(
  'chatStore',
  () => {
    const api = useApi();
    const snackbar = useSnackbarStore();
    const route = useRoute();

    const messages = ref<ChatMessage[]>([]);
    const isSending = ref(false);
    const error = ref<string | null>(null);
    const chatId = ref(uuidv4());

    // Getters
    

    const isLoading = computed(() => isSending.value);

    // Actions
    async function sendMessage(message: string) {
      if (!message.trim()) return;

      try {
        isSending.value = true;
        error.value = null;

        // Add user message
        addMessage({
          sender: 'user',
          text: message.trim(),
          timestamp: new Date(),
          loading: false
        });

        // Add temporary bot message
        addMessage({
          sender: 'bot',
          text: '',
          timestamp: new Date(),
          loading: true
        });

        await sendToBot(message.trim());
      } catch (err) {
        handleError(err);
      } finally {
        isSending.value = false;
        updateLastBotMessage({ loading: false });
      }
    }

    async function sendToBot(message: string) {
      try {
        const headers = {
          'Content-Type': 'application/json'
        } as Record<string, string>;

        try {
          const token = await api.getAuthToken();
          if (token) {
            headers['Authorization'] = `Bearer ${token}`;
          }
        } catch (error) {
          console.log(error);
        }
        const id = route.params.id;
        console.log(id);
        const response = await fetch(`${import.meta.env.VITE_API_URL}/agents/${id}/chats`, {
          method: 'POST',
          headers: headers,
          body: JSON.stringify({ chat: message, chatId: chatId.value })
        });

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const reader = response.body?.getReader();
        if (!reader) throw new Error('No response body');

        const decoder = new TextDecoder();
        let buffer = '';

        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          buffer += decoder.decode(value, { stream: true });
          updateLastBotMessage({ text: buffer });
        }

        // Final update
        updateLastBotMessage({ text: buffer });
      } catch (err) {
        console.error('Error sending message:', err);
        throw new Error('Failed to send message: ' + (err instanceof Error ? err.message : 'Unknown error'));
      }
    }

    // Helper functions
    function addMessage(message: ChatMessage) {
      messages.value = [...messages.value, message]; // ✅ Ensures Vue updates UI
    }

    function updateLastBotMessage(update: Partial<ChatMessage>) {
      const lastIndex = messages.value.length - 1;
      if (lastIndex >= 0 && messages.value[lastIndex].sender === 'bot') {
        messages.value[lastIndex] = { ...messages.value[lastIndex], ...update };
        messages.value = [...messages.value];
      }
    }

    function handleError(err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Unknown error occurred';
      error.value = errorMessage;
      snackbar.showSnackbar(errorMessage, 'error', 5000, 'Error');
      updateLastBotMessage({
        text: `${errorMessage}`,
        loading: false
      });
    }

    function clearChat() {
      messages.value = [];
      error.value = null;
    }

    // onUnmounted(clearChat);

    return {
      messages,
      isLoading,
      error,
      sendMessage,
      addMessage,
      clearChat
    };
  },
  {
    persist: true
  }
);

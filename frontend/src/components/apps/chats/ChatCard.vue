<template>
  <v-card class="px-4 d-flex flex-column" rounded="lg" style="height: 86vh">
    <v-card-title class="d-flex align-center">
      <v-avatar size="40" class="mr-3">
        <v-icon :icon="mdiRobotOutline"></v-icon>
      </v-avatar>
      <span>{{ agent?.name }}</span>
    </v-card-title>

    <v-divider></v-divider>

    <v-card-text ref="chatContainer" class="chat-container flex-grow-1 overflow-auto">
      <div v-for="(message, index) in chatStore.messages" :key="index">
        <!-- Bot Message -->
        <v-row v-if="message.sender === 'bot'">
          <v-col cols="12" class="d-flex justify-start align-start">
            <v-avatar size="40" class="mr-3">
              <v-icon color="primary" :icon="mdiRobot"></v-icon>
            </v-avatar>
            <v-chip color="grey-lighten-4" text-color="black" class="chat-message" variant="flat">
              <span v-html="marked(message.text)"></span>
              <div v-if="message.loading" class="typing-dots">
                <span class="dot"></span>
                <span class="dot"></span>
                <span class="dot"></span>
              </div>
            </v-chip>
          </v-col>
        </v-row>

        <!-- User Message -->
        <v-row v-else>
          <v-col cols="12" class="d-flex justify-end">
            <v-chip color="primary" text-color="white" class="chat-message">
              {{ message.text }}
            </v-chip>
            <v-avatar size="40" class="ml-3">
              <v-icon :icon="mdiAccountCircle"></v-icon>
            </v-avatar>
          </v-col>
        </v-row>
      </div>
    </v-card-text>

    <v-divider></v-divider>

    <v-card-actions class="d-flex align-center">
      <v-textarea
        variant="outlined"
        bg-color="grey-lighten-4"
        v-model="userInput"
        density="default"
        hide-details
        placeholder="Type your message..."
        :disabled="chatStore.isLoading"
        class="flex-grow-1 custom-text-field"
        rows="1"
        auto-grow
        @keydown="handleEnter"
      ></v-textarea>

      <v-btn
        color="primary"
        style="height: 40px; min-width: 48px"
        @click="sendMessage"
        :loading="chatStore.isLoading"
        :disabled="!userInput"
        variant="tonal"
      >
        <v-icon :icon="mdiSend"></v-icon>
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, toRefs } from 'vue';
import { mdiAccountCircle, mdiRobot, mdiRobotOutline, mdiSend } from '@mdi/js';
import { useChatStore } from '@/stores/apps/chatStore';
import { useAgentStore } from '@/stores/apps/agentStore';
const { agent } = toRefs(useAgentStore());
const chatStore = useChatStore();
const userInput = ref('');
const chatContainer = ref<HTMLElement | null>(null);
import { marked } from 'marked';

import { onUnmounted } from 'vue';

const handleEnter = (event: KeyboardEvent) => {
  if (event.shiftKey) {
    return;
  }
  if (!event.shiftKey && event.key === 'Enter') {
    event.preventDefault(); // Prevent form submission
    if (userInput.value.trim()) {
      sendMessage(); // Send the message
    }
  }

};

const sendMessage = async () => {
  if (!userInput.value.trim()) return;

  try {
    await chatStore.sendMessage(userInput.value);
    userInput.value = '';
    scrollToBottom();
  } catch (error) {
    console.error('Error sending message:', error);
  }
};

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
    }
  });
};

// Auto-scroll when new messages are added
onMounted(() => {
  chatStore.clearChat();
  if (chatStore.messages.length === 0) {
    chatStore.addMessage({
      sender: 'bot',
      text: 'Hi, How can I help you today?',
      timestamp: new Date(),
      loading: false
    });
  }
  scrollToBottom();
});

// Cleanup when component unmounts
onUnmounted(() => {
  // chatStore.clearChat();
});
</script>

<style scoped>
.chat-container {
  max-height: calc(100vh - 200px);
  /* padding: 12px; */
}

.chat-message {
  max-width: 80%;
  word-wrap: break-word;
  white-space: pre-wrap;
  padding: 12px 24px;
  margin: 8px 0;
  border-radius: 18px;
  position: relative;
}

.typing-dots {
  display: inline-flex;
  align-items: center;
  height: 100%;
  margin-left: 0px;
}

.dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  margin: 0 2px;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.dot:nth-child(2) {
  animation-delay: 0.2s;
}

.dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%,
  100% {
    transform: translateY(0);
    opacity: 0.6;
  }
  50% {
    transform: translateY(-3px);
    opacity: 1;
  }
}

.v-card-actions {
  margin-top: auto;
  padding: 12px 0;
}

.v-chip {
  height: auto !important;
  white-space: normal;
}
.custom-text-field .v-field__control {
  margin: 80px !important; /* Adjust as needed */
}
</style>

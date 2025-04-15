<script setup lang="js">
import { onMounted, ref } from 'vue';
import { toSvg } from 'jdenticon';
import { useApi } from '@/composables/useApi';
import AgentFormDialog from './AgentFormDialog.vue';
import { format, parseISO } from 'date-fns';
import { mdiLockOpenCheckOutline, mdiLockOutline, mdiOpenInNew } from '@mdi/js';
const api = useApi();
const agents = ref([]);
const dialog = ref(false);
const page = ref({ title: 'Your Agents' });

const getJdenticonSvg = (title) => {
  const svg = toSvg(title, 100);
  return `data:image/svg+xml;base64,${btoa(svg)}`;
};
const loading = ref(false);

const fetchAgents = async () => {
  try {
    loading.value = true;
    const response = await api.get('/agents');
    agents.value = response;
  } catch (err) {
    console.error('Failed to fetch items:', err);
  }finally{
    loading.value = false;
  }
};
const saveAgent = async (agent) => {
  agents.value.push(agent);
};
const formatDate = (date) => {
  return format(parseISO(date), 'dd.MM.yyyy');
};
onMounted(fetchAgents);
</script>

<template>
  
  <v-container>
    
    <v-row align="center" justify="space-between" class="mb-5">
      <h3 class="text-h3 ma-0">{{ page.title }}</h3>
      <v-btn v-if="agents.length > 0" color="primary" variant="elevated" @click="dialog = true" :loading="loading"> New Agent </v-btn>
    </v-row>


    <v-row justify="start" v-if="agents.length > 0">
      <v-col v-for="agent in agents" :key="agent.id" cols="12" sm="6" md="4" lg="3">
        <v-card
          :to="{ name: 'Chats', params: { id: agent.id } }"
          class="mx-auto"
          :prepend-avatar="getJdenticonSvg(agent.id)"
          :append-icon="mdiOpenInNew"
          :title="agent.name"
          density="compact"
        >
          <template #subtitle>
            <span class="text-subtitle-1">{{ formatDate(agent.timing.createdAt) }}</span>
          </template>
          <v-divider></v-divider>
          <div class="d-flex justify-space-between py-2">
            <v-btn
              :prepend-icon="agent.public ? mdiLockOpenCheckOutline : mdiLockOutline"
              :text="agent.public ? 'Public' : 'Private'"
              variant="text"
            ></v-btn>
          </div>
        </v-card>
      </v-col>
    </v-row>
    <v-col v-else cols="12">
      <v-card class="mx-auto text-center pa-5" variant="text">
        <v-card-title>No agents available</v-card-title>
        <v-card-subtitle>Please create an agent to get started.</v-card-subtitle>

        <v-btn color="primary" @click="dialog = true" class="mt-4" :loading="loading"> New Agent </v-btn>
      </v-card>
    </v-col>

    <agent-form-dialog v-model:show="dialog" @save="saveAgent" />
  </v-container>
</template>

<style scoped>
.fill-height {
  min-height: 0;
  /* Ensure the card doesn’t stretch unnecessarily */
}

.v-card-title {
  word-break: break-word;
  /* Ensure long titles wrap properly */
}
</style>

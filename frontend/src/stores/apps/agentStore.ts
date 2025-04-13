import { defineStore } from 'pinia';
import { useApi } from '@/composables/useApi';
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { router } from '@/router';
import type { Agent } from '@/types/agent';

const api = useApi();

export const useAgentStore = defineStore(
  'agentStore',
  () => {
    const agent = ref<Agent | null>(null);
    const loading = computed(() => api.loading);
    const error = computed(() => api.error);
    async function fetchAgent() {
      const route = useRoute();
      const id = route.params.id as string;
      agent.value = await api.get(`/agents/${id}`);
    }

    async function updateAgentName(data: unknown) {
      if (!agent.value) return;
      const updatedAgent = await api.patch<Agent>(`/agents/${agent.value.id}/name`, data);
      agent.value = updatedAgent;
    }

    async function updateAgentConfig(data: unknown) {
      if (!agent.value) return;
      const updatedAgent = await api.patch<Agent>(`/agents/${agent.value.id}/config`, data);
      agent.value = updatedAgent;
    }
    async function deleteAgent() {
      if (!agent.value) return;
      await api.del<Agent>(`/agents/${agent.value.id}`);
      router.replace('/agents');
    }
    
    return { agent, loading, error, fetchAgent: fetchAgent, updateAgentName, updateAgentConfig, deleteAgent };
  },
  {
    persist: {
      storage: sessionStorage,
      pick: ['agent']
    }
  }
);

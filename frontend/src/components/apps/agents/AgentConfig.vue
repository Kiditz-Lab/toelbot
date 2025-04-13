<template>
  <v-form ref="formRef" @submit.prevent="save">
    <UiParentCard title="Model">
      <template #action>
        <v-btn color="primary" variant="tonal" type="submit" :loading="loading"> Save </v-btn>
      </template>
      <v-label>Model</v-label>
      <v-autocomplete
        v-model="form.aiModel"
        :items="models"
        item-title="name"
        item-value="value"
        variant="outlined"
        class="mt-2"
      ></v-autocomplete>

      <!-- Prompt Textarea -->
      <v-label>Prompt</v-label>
      <v-textarea v-model="form.prompt" variant="outlined" rows="12" auto-grow class="mt-2"></v-textarea>

      <v-label>Temperature</v-label>
      <v-slider v-model.number="form.temperature" min="0" max="1" thumb-label step="0.1" color="primary"></v-slider>
    </UiParentCard>
  </v-form>
  <v-snackbar v-model="snackbar" timeout="3000" color="info" variant="flat">
    <v-row align="center">
      <v-col cols="auto">
        <v-icon :icon="mdiCheckCircle" color="white"></v-icon>
        <!-- Icon check -->
      </v-col>
      <v-col>
        <span class="headline">Success</span>
        <!-- Title "Success" -->
        <div>Your changes are saved!</div>
      </v-col>
    </v-row>

    <template v-slot:actions>
      <v-btn color="white" variant="tonal" @click="snackbar = false">Close</v-btn>
    </template>
  </v-snackbar>
</template>

<script setup lang="ts">
import { mdiCheckCircle } from '@mdi/js'

import UiParentCard from '@/components/shared/UiParentCard.vue';
import { useApi } from '@/composables/useApi';
import { ref, onMounted, toRefs, watch } from 'vue';
import type { AIModel } from '@/types/ai_model';
import { useAgentStore } from '@/stores/apps/agentStore';
const { agent } = toRefs(useAgentStore());
const { updateAgentConfig } = useAgentStore();
const loading = ref(false);
const formRef = ref<HTMLFormElement | null>(null);
const form = ref({
  aiModel: '',
  prompt: '',
  temperature: 0.0
});
watch(
  () => agent.value,
  (newAgent) => {
    if (newAgent) {
      form.value.aiModel = newAgent.config.aiModel;
      form.value.prompt = newAgent.config.prompt;
      form.value.temperature = newAgent.config.temperature;
    }
  },
  { immediate: true }
);

const snackbar = ref(false);
const models = ref([] as AIModel[]);
const api = useApi();

const fetchModels = async () => {
  const response = await api.get<AIModel[]>('/agents/models');
  models.value = response;
  if (!form.value.aiModel) {
    const defaultModel = models.value.find((model) => model.version === 'gpt-4o-mini');
    form.value.aiModel = defaultModel ? defaultModel.value : models.value[0]?.value || '';
  }
};
onMounted(fetchModels);
// Submit function
const save = async () => {
  if (formRef.value) {
    snackbar.value = false;
    loading.value = true;
    const { valid } = await formRef.value.validate();
    if (!valid) return;
    await updateAgentConfig(form.value);
    loading.value = false;
    snackbar.value = true;
  }
};
</script>

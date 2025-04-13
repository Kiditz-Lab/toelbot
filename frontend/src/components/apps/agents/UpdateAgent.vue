<template>
  <v-form ref="formRef" @submit.prevent="save">
    <v-label>Enter your agent name</v-label>
    <v-text-field
      v-model="form.name"
      hide-details="auto"
      variant="outlined"
      class="mt-2"
      placeholder="Enter your agent name"
      :rules="[nameRequired]"
      @keyup.enter="save"
    />
    <v-switch v-model="form.isPublic" label="Make It Public" color="primary" class="mt-3" />

    <v-btn :loading="loading" type="submit" color="primary" class="mt-3" variant="tonal">Save</v-btn>
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
  </v-form>
</template>
<script setup lang="ts">
import { mdiCheckCircle } from '@mdi/js';
import { useAgentStore } from '@/stores/apps/agentStore';
import { ref, toRefs, watch } from 'vue';
const snackbar = ref(false);
const { loading, agent } = toRefs(useAgentStore());
const { updateAgentName } = useAgentStore();

const form = ref({
  name: '',
  isPublic: false,
});

watch(
  () => agent.value,
  (newAgent) => {
    if (newAgent) {
      form.value.name = newAgent.name;
      form.value.isPublic = newAgent.public;
    }
  },
  { immediate: true }
);

// Ref untuk <v-form>
const formRef = ref<HTMLFormElement | null>(null);

// Validasi
const nameRequired = (value: string) => !!value || 'Name is required';

// Handle submit
const save = async () => {
  if (formRef.value) {
    const { valid } = await formRef.value.validate();
    if (!valid) return;
    await updateAgentName(form.value);
    snackbar.value = true;
  }
};
</script>

<script setup lang="ts">
import { useToolStore } from '@/stores/apps/toolStore';
// import type { ParsedArgField } from '@/types/tool';
import { ref, toRef } from 'vue';
const toolStore = useToolStore();
const tool = toRef(toolStore, 'selectedTool');
const envValues = toRef(toolStore, 'envValues');
// const argsValues = toRef(toolStore, 'envValues');
const agentTools = toRef(toolStore, 'agentTools');
const loading = toRef(toolStore, 'loading');
const formRef = toRef(toolStore, 'formRef');


// const disableTestConnection = computed(() => toolStore.disableTestConnection);

const { saveConnection, updateConnection } = useToolStore();
const isValid = ref<boolean>(true);

const getComponent = (type: string) => {
  switch (type) {
    case 'text_field':
    case 'number':
      return 'v-text-field';
    case 'text_area':
      return 'v-textarea';
    default:
      return 'template';
  }
};

const formatLabel = (key: string) => {
  return key
    .toLowerCase()
    .replace(/_/g, ' ')
    .replace(/\b\w/g, (c) => c.toUpperCase());
};
const handleSaveOrUpdate = async () => {
  if (agentTools.value.includes(tool.value.id)) {
    await updateConnection();
  } else {
    await saveConnection();
  }
};
</script>
<template>
  <v-form ref="formRef" v-model="isValid" @submit.prevent="handleSaveOrUpdate">
    <v-row dense>
      <v-col v-for="(type, key) in tool.env" :key="key" cols="12">
        <component
          :is="getComponent(type)"
          v-model="envValues[key]"
          :label="formatLabel(key)"
          :type="type === 'number' ? 'number' : 'text'"
          :rules="[(v: string) => !!v || 'This field is required']"
          variant="outlined"
          required
        />
      </v-col>
      <!-- <v-col v-for="(arg, index) in parseArgs(tool.args)" :key="'arg-' + index" cols="12">
        <component
          :is="getComponent(arg.type)"
          v-model="argsValues[arg.key]"
          :label="formatLabel(arg.key)"
          :type="arg.type === 'number' ? 'number' : 'text'"
          :rules="[(v: string) => !!v || 'This field is required']"
          variant="outlined"
          required
        />
      </v-col> -->
    </v-row>
    <!-- {{ disableTestConnection }} -->
    <v-btn
      v-if="!agentTools.includes(tool.id)"
      type="submit"
      :loading="loading"
      text="Connect Now"
      color="primary"
      block
      
    ></v-btn>
    <v-btn :loading="loading" v-else type="submit" text="Update Connection" color="success" block ></v-btn>
  </v-form>
</template>

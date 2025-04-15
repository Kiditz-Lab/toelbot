<script setup lang="ts">
import ToolList from './ToolList.vue';
import BaseBreadcrumb from '@/components/shared/BaseBreadcrumb.vue';
import { useAgentStore } from '@/stores/apps/agentStore';
import { useToolStore } from '@/stores/apps/toolStore';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
const { fetchTools } = useToolStore();
const { fetchAgent } = useAgentStore();
const route = useRoute();

onMounted(async () => {
  console.log('Mounted');
  const agentId = route.params.id as string;  
  await fetchTools();
  await fetchAgent(agentId);
});
const breadcrumbs = ref([{ title: 'Tools', disabled: false, href: '#' }]);
</script>
<template>
  <BaseBreadcrumb title="Tools" :breadcrumbs="breadcrumbs"></BaseBreadcrumb>
  <v-row justify="start">
    <v-col cols="12">
      <ToolList />
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import ToolList from './ToolList.vue';
import BaseBreadcrumb from '@/components/shared/BaseBreadcrumb.vue';
import { useToolStore } from '@/stores/apps/toolStore';
import { onMounted, ref } from 'vue';
const { fetchTools } = useToolStore();
onMounted(fetchTools);
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

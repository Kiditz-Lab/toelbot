<script setup>
import { useTrainingWebsiteStore } from '@/stores/apps/trainingWebsiteStore';

import { toRefs } from 'vue';
import { format } from 'date-fns';
import { useFile } from '@/composables/useFile';
// const store = useTrainingWebsiteStore();
const { trainings, options, totalItems, loading, selectedItems } = toRefs(useTrainingWebsiteStore());
const { formatFileSize } = useFile();
const getStatusColor = (status) => {
  switch (status) {
    case 'PENDING':
      return 'info';
    case 'ERROR':
      return 'error';
    case 'TRAINED':
      return 'success';
    default:
      return 'default';
  }
};

const formatStatus = (status) => {
  return status.charAt(0).toUpperCase() + status.slice(1).toLowerCase();
};
const formatDate = (isoString) => {
  return format(new Date(isoString), 'MMM dd/yyyy h:mm a');
};
</script>
<template>
  <!-- eslint-disable vue/valid-v-slot -->
  <v-data-table-server
    v-model:items-per-page="options.itemsPerPage"
    v-model:page="options.page"
    v-model:sort-by="options.sortBy"
    :items="trainings"
    :loading="loading"
    :items-length="totalItems"
    v-model="selectedItems"
    show-select
    class="elevation-0"
    :headers="[
      { key: 'status', title: 'STATUS', sortable: false },
      { key: 'url', title: 'DATASOURCE', sortable: true },
      { key: 'size', title: 'SIZE', sortable: true },
      { key: 'createdAt', title: 'CREATED', sortable: true }
    ]"
    :items-per-page-options="[
      { value: 20, title: '20' },
      { value: 50, title: '50' },
      { value: 100, title: '100' },
      { value: 200, title: '200' },
      { value: 400, title: '400' },
      { value: 600, title: '600' }
    ]"
  >
    <template v-slot:item.status="{ item }">
      <v-chip :color="getStatusColor(item.status)" label>
        <v-icon start v-if="item.progress">
          <v-progress-circular indeterminate :color="getStatusColor(item.status)" size="x-large"></v-progress-circular>
        </v-icon>

        {{ formatStatus(item.status) }}
      </v-chip>
    </template>
    <template v-slot:item.url="{ item }">
      <a :href="item.url" v-if="!item.website" target="_blank" class="text-decoration-none text-black">{{ item.url }}</a>
      <v-card v-else variant="text" density="compact" :href="item.url" target="_blank" class="ma-1">
        <template v-slot:prepend>
          <v-avatar class="mr-3">
            <v-img :src="item.website.image" alt="Website image" />
          </v-avatar>
        </template>
        <template v-slot:title>
          <div class="text-subtitle-2 font-weight-bold text-truncate">
            {{ item.website.title }}
          </div>
        </template>
        <template v-slot:subtitle>
          {{ item.website.description }}
        </template>
      </v-card>
    </template>
    <template v-slot:item.createdAt="{ item }">
      {{ formatDate(item.createdAt) }}
    </template>

    <template v-slot:item.size="{ item }">
      {{ formatFileSize(item.size) }}
    </template>
  </v-data-table-server>
</template>
<style>
tbody tr:nth-of-type(odd) {
  background-color: rgba(0, 0, 0, 0.06) !important;
}
</style>

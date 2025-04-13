<script setup>
import { useChatHistoryStore } from '@/stores/apps/chatHistoryStore';
import { onMounted, toRefs } from 'vue';
import { formatDistanceToNow } from 'date-fns';

const store = useChatHistoryStore();
const { histories, totalItems, loading, options, fetchHistories } = toRefs(store);

onMounted(() => {
  fetchHistories.value();
});

const getFlagEmoji = (countryCode) => {
  if (!countryCode) return countryCode;
  return countryCode
    .toUpperCase()
    .split('')
    .map((c) => String.fromCodePoint(127397 + c.charCodeAt(0)))
    .join('');
};

// Format timestamps to "time ago"
const timeAgo = (date) => {
  return formatDistanceToNow(new Date(date), { addSuffix: true });
};
</script>

<template>
  <!-- eslint-disable vue/valid-v-slot -->
  <v-data-table-server
    fixed-header
    v-model:items-per-page="options.itemsPerPage"
    v-model:page="options.page"
    v-model:sort-by="options.sortBy"
    :items="histories"
    :headers="[
      { key: 'country', title: 'Country' },
      { key: 'createdAt', title: 'Created At', sortable: true },
      { key: 'userMessage', title: 'Message' },
      { key: 'model', title: 'LLM' }
    ]"
    :loading="loading"
    :items-length="totalItems"
  >
    <template v-slot:item.country="{ item }">
      <span>{{ getFlagEmoji(item.countryCode) }} {{ item.country }}</span>
    </template>
    <template v-slot:item.createdAt="{ value }">
      <span>{{ timeAgo(value) }}</span>
    </template>
  </v-data-table-server>
</template>

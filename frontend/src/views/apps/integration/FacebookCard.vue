<script setup lang="ts">
import { useAgentStore } from '@/stores/apps/agentStore';
import { useFacbookStore } from '@/stores/apps/facebookStore';
import { useSnackbarStore } from '@/stores/snackbarStore';
import { mdiPowerPlug, mdiPowerPlugOff } from '@mdi/js';
import { onMounted, toRefs } from 'vue';
const store = useFacbookStore();
const agentStore = useAgentStore();
const { pages } = toRefs(store);
const { agent } = toRefs(agentStore);
onMounted(() => {
  const { showSnackbar } = useSnackbarStore();
  
  window.addEventListener('message', (event) => {
    const { type, payload } = event.data;
    console.log(type);
    if (type === 'facebook-connected') {
      if (payload.status === 'success') {
        store.setPages(payload.pages);
        console.log('Facebook Pages:', payload.pages);
      } else {
        showSnackbar('Failed to connect', 'error', 3000, 'Error');
      }
    }
  });
});
</script>

<template>
  <v-card title="Facebook" subtitle="Integrate Toelbot to Facebook">
    <template v-slot:prepend>
      <img src="@/assets/tools/facebook.svg" />
    </template>
    <v-card-text> Connect your Toelbot to your Facebook Pages to chat with your customer 24/7.</v-card-text>
    <v-card-actions v-if="agent">
      <v-btn :disabled="agent?.facebooks?.length > 0" color="primary" variant="elevated" block @click="store.connectFacebook">Connect</v-btn>
    </v-card-actions>
    <v-card-text class="ma-0">
      <v-list-item
        divider="bottom"
        v-for="page in pages"
        :key="page.id"
        :title="page.name"
        :subtitle="page.category"
        :prepend-avatar="page.imageUrl"
      >
        <template #append>
          <v-tooltip :text="agent?.facebooks?.includes(page.pageId) ? 'Unsubscribe' : 'Subscribe'" bottom>
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                :color="agent?.facebooks?.includes(page.pageId) ? 'success' : 'secondary'"
                :icon="agent?.facebooks?.includes(page.pageId) ? mdiPowerPlugOff : mdiPowerPlug"
                variant="tonal"
                @click="!agent?.facebooks?.includes(page.pageId) ? store.assignPage(page) : store.unassignPage(page)"
                :loading="page.loading"
              />
            </template>
          </v-tooltip>
        </template>
      </v-list-item>
    </v-card-text>
  </v-card>
</template>

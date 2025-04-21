<script setup lang="ts">
import { useFacbookStore } from '@/stores/apps/facebookStore';
import { mdiPowerPlug, mdiPowerPlugOff } from '@mdi/js';
import { toRefs } from 'vue';
const store = useFacbookStore();
const { pages } = toRefs(store);
</script>

<template>
  <v-card title="Facebook" subtitle="Integrate Toelbot to Facebook">
    <template v-slot:prepend>
      <v-avatar rounded>
        <img src="@/assets/tools/facebook.svg" />
      </v-avatar>
    </template>
    <v-card-text> Connect your Toelbot to your Facebook Pages to chat with your customer 24/7.</v-card-text>
    <v-card-actions>
      <v-btn  color="primary" variant="flat" block @click="store.connectFacebook"
        >{{ pages.length > 0 ? 'Reconnect' : 'Connect' }}</v-btn
      >
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
          <v-tooltip :text="page.active ? 'Unsubscribe' : 'Subscribe'" bottom>
            <template #activator="{ props }">
              <v-btn
              v-bind="props"
              :color="page.active ? 'success' : 'secondary'"
              :icon="page.active ? mdiPowerPlugOff : mdiPowerPlug"
              variant="tonal"
              @click="!page.active ? store.assignPage(page) : store.unassignPage(page)"
                :loading="page.loading"
              />
            </template>
          </v-tooltip>
        </template>
      </v-list-item>
    </v-card-text>
  </v-card>
</template>

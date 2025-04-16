<script setup lang="ts">
import { useAgentStore } from '@/stores/apps/agentStore';
import { useFacbookStore } from '@/stores/apps/facebookStore';
import { mdiPowerPlug, mdiPowerPlugOff } from '@mdi/js';
import { toRefs } from 'vue';
const store = useFacbookStore();
const agentStore = useAgentStore();
const { pages } = toRefs(store);
const { agent } = toRefs(agentStore);
</script>

<template>
  <v-card title="Facebook" subtitle="Integrate Toelbot to Facebook">
    <template v-slot:prepend>
      <v-avatar rounded>
        <img src="@/assets/tools/facebook.svg" />
      </v-avatar>
    </template>
    <v-card-text> Connect your Toelbot to your Facebook Pages to chat with your customer 24/7.</v-card-text>
    <v-card-actions v-if="agent">
      <v-btn :disabled="agent?.facebooks?.length > 0" color="primary" variant="elevated" block @click="store.connectFacebook"
        >Connect</v-btn
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

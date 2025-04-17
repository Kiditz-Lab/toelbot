<script setup lang="ts">
import { useInstagramStore } from '@/stores/apps/instagramStore';
import { toRefs } from 'vue';
import { mdiPowerPlug, mdiPowerPlugOff } from '@mdi/js';
const store = useInstagramStore();

const { account } = toRefs(store);
</script>
<template>
  <v-card title="Instagram" subtitle="Integrate Toelbot to Instagram">
    <template v-slot:prepend>
      <v-avatar rounded>
        <v-img src="https://upload.wikimedia.org/wikipedia/commons/e/e7/Instagram_logo_2016.svg" contain />
      </v-avatar>
    </template>
    <v-card-text> Connect your Toelbot to your Instagram <b>Creator/Business</b> Account to chat with your customer 24/7.</v-card-text>
    <v-card-actions>
      <v-btn color="pink" variant="flat" block @click="store.connectInstagram">{{
        account ? 'Reconnect' : 'Connect'
      }}</v-btn>
    </v-card-actions>
    <v-card-text v-if="account">
      <v-list-item
        divider="bottom"
        :key="account?.id"
        :title="account?.name"
        :subtitle="account?.username"
        :prepend-avatar="account?.profilePictureUrl"
      >
        <template #append>
          <v-tooltip :text="account.active ? 'Unsubscribe' : 'Subscribe'" bottom>
            <template #activator="{ props }">
              <v-btn
                v-bind="props"
                :color="account.active ? 'success' : 'secondary'"
                :icon="account.active ? mdiPowerPlugOff : mdiPowerPlug"
                variant="tonal"
                @click="account.active ? store.unsubscribe(account) : store.subscribe(account)"
                :loading="account.loading"
              />
            </template>
          </v-tooltip>
        </template>
      </v-list-item>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { useFacbookStore } from '@/stores/apps/facebookStore';
import { mdiOpenInNew } from '@mdi/js';
const store = useFacbookStore();
</script>

<template>
  <v-card title="Facebook" subtitle="Integrate Toelbot to Facebook" :append-icon="mdiOpenInNew">
    <template v-slot:prepend>
      <img src="@/assets/tools/facebook.svg" />
    </template>
    <v-card-text> Connect your Toelbot to your Facebook Pages to chat with your customer 24/7. </v-card-text>
    <v-card-actions>
      <v-btn color="primary" variant="elevated" block @click="store.connectFacebook">Connect</v-btn>
    </v-card-actions>
  </v-card>
</template>

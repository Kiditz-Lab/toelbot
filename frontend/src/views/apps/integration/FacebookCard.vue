<script setup lang="ts">
import { useFacbookStore } from '@/stores/apps/facebookStore';
import { mdiOpenInNew } from '@mdi/js';
import { onMounted } from 'vue';
const store = useFacbookStore();
onMounted(() => {
  window.addEventListener('message', (event) => {
    
    
    if (event.origin !== 'https://toelbox.com' || event.origin !== 'http://localhost:5173') return;

    const { type, payload } = event.data;
    console.log(payload);
    if (type === 'facebook-connected') {
      if (payload.status === 'success') {
        console.log('Facebook Pages:', payload.pages);
        // render them in the UI or save them to a store
      } else {
        console.error('Failed to connect:', payload.message);
      }
    }
  });
});
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

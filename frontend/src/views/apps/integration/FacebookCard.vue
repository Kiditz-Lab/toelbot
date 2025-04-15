<script setup lang="ts">
import { useFacbookStore } from '@/stores/apps/facebookStore';
import { useSnackbarStore } from '@/stores/snackbarStore';
import { mdiConnection, mdiOpenInNew } from '@mdi/js';
import { onMounted, toRefs } from 'vue';
const store = useFacbookStore();
const { pages } = toRefs(store);
onMounted(() => {
  const { showSnackbar } = useSnackbarStore();

  window.addEventListener('message', (event) => {
    // if (event.origin !== 'https://toelbox.com') return;
    const { type, payload } = event.data;
    console.log(type);
    if (type === 'facebook-connected') {
      if (payload.status === 'success') {
        store.setPages(payload.pages);
        console.log('Facebook Pages:', payload.pages);
        // render them in the UI or save them to a store
      } else {
        showSnackbar('Failed to connect', 'error', 3000, 'Error');
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
    <v-card-text class="ma-0">
      <v-list-item divider="bottom"
        v-for="page in pages"
        :key="page.id"
        :title="page.name"
        :subtitle="page.category"
        :prepend-avatar="page.picture.data.url"
        
      >
        <template #append>
          <v-tooltip text="Assign" bottom>
            <template #activator="{ props }">
              <v-btn v-bind="props" color="" :icon="mdiConnection"  variant="tonal"/>
            </template>
          </v-tooltip>
        </template>
      </v-list-item>
    </v-card-text>
  </v-card>
</template>

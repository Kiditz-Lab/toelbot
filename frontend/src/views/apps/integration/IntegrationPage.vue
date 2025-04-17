<script setup lang="ts">
import { onMounted, ref } from 'vue';
import FacebookCard from './FacebookCard.vue';
import InstagramCard from './InstagramCard.vue';
import BaseBreadcrumb from '@/components/shared/BaseBreadcrumb.vue';
import { useSnackbarStore } from '@/stores/snackbarStore';
import { useFacbookStore } from '@/stores/apps/facebookStore';
import { useInstagramStore } from '@/stores/apps/instagramStore';
const facebookStore = useFacbookStore();
const instagramStore = useInstagramStore();

const breadcrumbs = ref([{ title: 'Integration', disabled: false, href: '#' }]);
const { showSnackbar } = useSnackbarStore();
onMounted(async() => {
  await instagramStore.fetchByAgent();
  await facebookStore.fetchByAgent();
  window.addEventListener('message', (event) => {
    const { type, payload } = event.data;
    console.log(type);
    if (type === 'facebook-connected') {
      if (payload.status === 'success') {
        facebookStore.setPages(payload.pages);
        console.log('Facebook Pages:', payload.pages);
      } else {
        showSnackbar('Failed to connect', 'error', 3000, 'Error');
      }
    }
    if (type === 'instagram-connected') {
      if (payload.status === 'success') {
        // instagramStore.  (payload.pages);
        instagramStore.setAccount(payload.account);
        console.log('Instagram Account:', payload.account);
      } else {
        showSnackbar('Failed to connect to Instagram', 'error', 3000, 'Error');
      }
    }
  });
});
</script>

<template>
  <BaseBreadcrumb title="Integration" :breadcrumbs="breadcrumbs" />
  <v-row>
    <v-col cols="12" sm="6" md="4" lg="4">
      <FacebookCard />
    </v-col>
    <v-col cols="12" sm="6" md="4" lg="4">
      <InstagramCard />
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import { useInstagramStore } from '@/stores/apps/instagramStore';
import { toRefs } from 'vue';
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
      <v-btn color="pink" block variant="elevated" @click="store.connectInstagram">Connect</v-btn>
    </v-card-actions>
    <v-card-text>
      <v-list-item
        divider="bottom"
        :key="account?.id"
        :title="account?.name"
        :subtitle="account?.username"
        :prepend-avatar="account?.profile_picture_url"
      ></v-list-item>
    </v-card-text>
  </v-card>
</template>

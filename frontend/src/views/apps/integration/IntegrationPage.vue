<script setup lang="ts">
import { ref } from 'vue';
import FacebookCard from './FacebookCard.vue';
import InstagramCard from './InstagramCard.vue';
import BaseBreadcrumb from '@/components/shared/BaseBreadcrumb.vue';
const breadcrumbs = ref([{ title: 'Integration', disabled: false, href: '#' }]);
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

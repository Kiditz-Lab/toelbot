<script setup lang="ts">
import { useTrainingWebsiteStore } from '@/stores/apps/trainingWebsiteStore';
import { toRefs } from 'vue';

const { url, urlRules, loading, formRef } = toRefs(useTrainingWebsiteStore());
const { crawlWebsite } = useTrainingWebsiteStore();
</script>

<template>
  <v-form ref="formRef" @submit.prevent="crawlWebsite">
    <div class="d-flex align-center justify-center">
      <v-text-field v-model="url" :rules="urlRules" required variant="outlined" density="compact" label="URL">
        <template v-slot:append>
          <v-btn :loading="loading" color="black" density="comfortable" type="submit" class="h-100">Crawl</v-btn>
        </template>
      </v-text-field>
    </div>
  </v-form>
</template>

<style scoped>
.v-form {
  height: 100%;
}
</style>

<script setup lang="ts">
import { useFile } from '@/composables/useFile';
import { useTrainingFileStore } from '@/stores/apps/trainingFileStore';
import { useTrainingTextStore } from '@/stores/apps/trainingTextStore';
import { toRefs, onMounted } from 'vue';

const { size } = toRefs(useTrainingFileStore());
const { textSize } = toRefs(useTrainingTextStore());
const { fetchTrainingSize } = useTrainingFileStore();
const { formatFileSize } = useFile();
onMounted(async () => {
  await fetchTrainingSize();
});
</script>

<template>
  <v-row>
    <v-col cols="12" md="12">
      <v-card variant="outlined">
        <v-card-title class="text-h6 text-lightText">Total Size</v-card-title>
        <v-card-text>
          <div class="text-h6">{{ formatFileSize(size?.totalSize + textSize) }}</div>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
  <v-row>
    <v-col cols="12" md="6">
      <v-card variant="outlined">
        <v-card-title class="text-h6 text-lightText">Link</v-card-title>
        <v-card-text>
          <div class="text-h6">{{ formatFileSize(size?.urlSize) }}</div>
        </v-card-text>
      </v-card>
    </v-col>
    <v-col cols="12" md="6">
      <v-card variant="outlined">
        <v-card-title class="text-h6 text-lightText">Text</v-card-title>
        <v-card-text>
          <div class="text-h6">{{ formatFileSize(textSize) }}</div>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
  <v-row>
    <v-col cols="12" md="12">
      <v-card variant="outlined">
        <v-card-title class="text-h6 text-lightText">File</v-card-title>
        <v-card-text>
          <div class="text-h6">{{ formatFileSize(size?.fileSize) }}</div>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>

  <slot></slot>
</template>

<script setup lang="ts">
import TrainingSummary from '@/components/apps/training-data/TrainingSummary.vue';
import TrainingText from '@/components/apps/training-data/TrainingText.vue';
import UiParentCard from '@/components/shared/UiParentCard.vue';
import { useTrainingTextStore } from '@/stores/apps/trainingTextStore';
import { onMounted } from 'vue';
const { loading, fetchTrainingText } = useTrainingTextStore();
const store = useTrainingTextStore();
onMounted(() => fetchTrainingText());
</script>
<template>
  <v-row>
    <v-col cols="12" md="7">
      <v-row>
        <v-col cols="12" md="12">
          <UiParentCard title="Text">
            <TrainingText />
          </UiParentCard>
        </v-col>
      </v-row>
    </v-col>
    <v-col cols="12" md="5">
      <UiParentCard title="Sources">
        <training-summary>
          <v-btn :loading="loading" color="primary" class="mt-4" block variant="flat" @click="store.trainTextAgent">Training Agent</v-btn>
        </training-summary>
      </UiParentCard>
    </v-col>
  </v-row>
</template>

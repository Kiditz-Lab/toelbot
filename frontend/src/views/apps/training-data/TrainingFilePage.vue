<script setup lang="ts">
import { ref, toRefs, onMounted, onUnmounted } from 'vue';
import BaseBreadcrumb from '@/components/shared/BaseBreadcrumb.vue';
import UiParentCard from '@/components/shared/UiParentCard.vue';
import FileUpload from '@/components/shared/FileUpload.vue';
import TrainingList from '@/components/apps/training-data/TrainingFiles.vue';
import TrainingSummary from '@/components/apps/training-data/TrainingSummary.vue';
import { useTrainingFileStore } from '@/stores/apps/trainingFileStore';
const { fileChanges } = useTrainingFileStore();
const { trainAgent, loading } = toRefs(useTrainingFileStore());
const { fetchTrainingFile } = useTrainingFileStore();
const { connect, disconnect } = useTrainingFileStore();

const breadcrumbs = ref([{ title: 'Training Data', disabled: false, href: '#' }]);
onMounted(async () => {
  console.log('On Mounted');
  await fetchTrainingFile();
  connect();
});
onUnmounted(() => {
  console.log('On Unmounted');
  disconnect();
});


</script>
<template>
  <div>
    <BaseBreadcrumb title="Training Data" :breadcrumbs="breadcrumbs"></BaseBreadcrumb>
    <v-row>
      <v-col cols="12" md="7">
        <v-row>
          <v-col cols="12" md="12">
            <UiParentCard title="Upload Files">
              <FileUpload @upload="fileChanges" />
            </UiParentCard>
          </v-col>
          <v-col cols="12" md="12">
            <TrainingList />
          </v-col>
        </v-row>
      </v-col>
      <v-col cols="12" md="5">
        <UiParentCard title="Sources">
          <TrainingSummary>
            <v-btn :loading="loading" color="primary" class="mt-4" block variant="flat" @click="trainAgent">Training Agent</v-btn>
          </TrainingSummary>
        </UiParentCard>
      </v-col>
    </v-row>
  </div>
</template>
<style>
.v-divider__wrapper {
  display: flex;
  align-items: center;
  max-width: 0px;
  justify-content: center;
}
</style>

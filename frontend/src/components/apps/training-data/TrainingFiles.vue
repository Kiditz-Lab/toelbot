<script setup lang="ts">
import UiParentCard from '@/components/shared/UiParentCard.vue';
import DeleteDialog from '@/components/shared/DeleteDialog.vue';
import { useFile } from '@/composables/useFile';
import { useTrainingFileStore } from '@/stores/apps/trainingFileStore';
import { mdiDelete } from '@mdi/js';
import { onMounted, ref, toRefs } from 'vue';
import type { TrainingData } from '@/types/training_data';
const { deleteTrainingData, fetchTrainingFile } = useTrainingFileStore();
const { trainings, uploadProgress } = toRefs(useTrainingFileStore());
const { formatFileSize, getFileIcon } = useFile();
const getStatusColor = (status: string) => {
  switch (status) {
    case 'PENDING':
      return 'warning';
    case 'ERROR':
      return 'error';
    case 'TRAINED':
      return 'success';
    default:
      return 'default';
  }
};

const formatStatus = (status: string) => {
  return status.charAt(0).toUpperCase() + status.slice(1).toLowerCase();
};
const selectedTrainingData = ref<TrainingData | null>(null);
const dialog = ref(false);
const openDeleteDialog = (data: TrainingData) => {
  selectedTrainingData.value = data;
  dialog.value = true;
};

onMounted(fetchTrainingFile);
const confirmDelete = async () => {
  if (selectedTrainingData.value) {
    console.log(`Deleting training ID: ${selectedTrainingData.value.id}`);
    deleteTrainingData(selectedTrainingData.value.id);
    selectedTrainingData.value = null;
    dialog.value = false;
  }
};
</script>

<template>
  <UiParentCard title="Files" class="pa-0" v-if="trainings.length">
    <v-progress-linear
      v-if="uploadProgress > 0 && uploadProgress < 100"
      v-model="uploadProgress"
      color="primary"
    ></v-progress-linear>
    <v-list>
      <v-list-item v-for="training in trainings" :key="training.id" :title="training.url" :subtitle="formatFileSize(training.size)" divider>
        <template v-slot:prepend>
          <v-img :src="getFileIcon(training.url)" width="24" height="46" class="mx-3" />
        </template>
        <template v-slot:append>
          <v-progress-circular
            :width="2"
            :size="20"
            v-if="training.progress"
            color="primary"
            class="mr-2"
            indeterminate
          ></v-progress-circular>
          <v-chip :color="getStatusColor(training.status)" size="small" class="mr-2">{{ formatStatus(training.status) }}</v-chip>
          <v-btn :icon="mdiDelete" size="small" color="error" class="ml-2" variant="tonal" @click="openDeleteDialog(training)"></v-btn>
        </template>
      </v-list-item>
    </v-list>
  </UiParentCard>
  <DeleteDialog
    v-model:show="dialog"
    title="Warning!"
    :message="`Are you sure you want to delete \n'${selectedTrainingData?.url}' data?`"
    @confirm="confirmDelete"
  />
</template>

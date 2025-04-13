<script setup lang="ts">
import TrainingWebsite from '@/components/apps/training-data/TrainingWebsite.vue';
import TrainingWebsiteList from '@/components/apps/training-data/TrainingWebsiteList.vue';
import DeleteDialog from '@/components/shared/DeleteDialog.vue';
import UiParentCard from '@/components/shared/UiParentCard.vue';
import { useTrainingWebsiteStore } from '@/stores/apps/trainingWebsiteStore';
import { mdiMagnify } from '@mdi/js';
import { onBeforeMount, onUnmounted, toRefs } from 'vue';
const { trainings, loading, search, dialog, selectedItems} = toRefs(useTrainingWebsiteStore());
const { confirmDelete, fetchTrainingWebsite, connect, disconnect, trainWebsite, openDeleteDialog } = useTrainingWebsiteStore();

onBeforeMount(() => {
  fetchTrainingWebsite();
  connect();
});
onUnmounted(() => {
  disconnect();
});
</script>
<template>
  <v-row>
    <v-col cols="12" md="12">
      <v-row>
        <v-col cols="12" md="12">
          <UiParentCard title="Website">
            <TrainingWebsite />
          </UiParentCard>
        </v-col>
        <v-col cols="12" md="12">
          <UiParentCard v-if="trainings" title="Collected Links" class="pa-0">
            <template #action>
              <div class="d-flex align-center flex-wrap ga-2 w-50">
                <v-text-field
                  v-model="search"
                  density="compact"
                  placeholder="Search.."
                  hide-details
                  :prepend-inner-icon="mdiMagnify"
                  variant="outlined"
                  clearable
                />
                <v-btn :loading="loading" color="error" variant="flat" @click="openDeleteDialog" :disabled="selectedItems.length == 0"> Delete </v-btn>
                <v-btn :loading="loading" color="indigo" variant="flat" @click="trainWebsite" :disabled="selectedItems.length == 0"> Do Train Agent </v-btn>
              </div>

              <!-- <v-btn :loading="loading" color="black" variant="flat"  @click="store.trainWebsite">Do Train Agent</v-btn> -->
            </template>
            <TrainingWebsiteList />
          </UiParentCard>
        </v-col>
      </v-row>
    </v-col>
    <DeleteDialog
      v-model:show="dialog"
      title="Warning!"
      :message="`Are you sure you want to delete the selected links?`"
      @confirm="confirmDelete"
    />
    <!-- <v-col cols="12" md="5">
      <UiParentCard title="Sources">
        <training-summary>
          
        </training-summary>
      </UiParentCard>
    </v-col> -->
  </v-row>
</template>

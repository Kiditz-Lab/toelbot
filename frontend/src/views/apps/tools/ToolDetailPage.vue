<script setup lang="js">
import DeleteDialog from '@/components/shared/DeleteDialog.vue';
import { useToolStore } from '@/stores/apps/toolStore';
import { MdPreview } from 'md-editor-v3';
import { JsonViewer } from 'vue3-json-viewer';

import ToolForm from './ToolForm.vue';
// import ToolDelete from './ToolDelete.vue';

import { onMounted, ref, toRefs } from 'vue';
import { mdiDelete } from '@mdi/js';
const tab = ref('overview');
const dialog = ref(false);

const { selectedTool, testToolResult, loading, agentTools, toolId } = toRefs(useToolStore());
const { testConnection, getFields, deleteConnection, fetchMcp, findToolById } = useToolStore();

onMounted(async () => {
  import('md-editor-v3/lib/style.css');
  selectedTool.value = {};
  await findToolById(toolId.value);
  await fetchMcp(toolId.value);
});
</script>
<template>
  <v-row>
    <v-col cols="8">
      <v-card :title="selectedTool.name" :prepend-avatar="selectedTool.imageUrl" variant="flat" elevation="0" density="comfortable">
        <template #append v-if="agentTools.includes(selectedTool.id)">
          <v-btn color="error" @click="dialog = true" :prepend-icon="mdiDelete" text="Delete"></v-btn>
        </template>
        <v-card-text>
          {{ selectedTool.shortDescription }}
        </v-card-text>
        <v-tabs v-model="tab" bg-color="none" align-tabs="start" data-allow-mismatch>
          <v-tab value="overview">Overview</v-tab>
          <v-tab value="tools">Tools</v-tab>
        </v-tabs>

        <v-window v-model="tab">
          <v-window-item value="overview">
            <div class="pa-4">
              <MdPreview :model-value="selectedTool.description" preview-theme="github" />
            </div>
          </v-window-item>

          <v-window-item value="tools">
            <v-expansion-panels variant="accordion">
              <v-expansion-panel v-for="tool in selectedTool.tools" :key="tool.name">
                <v-expansion-panel-title>
                  {{ tool.name }}
                </v-expansion-panel-title>
                <v-expansion-panel-text>
                  <div class="text-subtitle-1 text-grey my-3">{{ tool.description }}</div>
                  <v-form @submit.prevent="testConnection(tool)" v-model="tool.isValid">
                    <template v-for="field in getFields(tool)" :key="field.name">
                      <v-text-field
                        v-if="field.type === 'number'"
                        variant="outlined"
                        v-model.number="tool.formData[field.name]"
                        :label="field.title"
                        type="number"
                        :rules="[field.required ? (v) => !!v || 'Required' : () => true]"
                      />
                      <v-text-field
                        v-else
                        variant="outlined"
                        v-model="tool.formData[field.name]"
                        :label="field.title"
                        type="text"
                        :rules="[field.required ? (v) => !!v || 'Required' : () => true]"
                      />
                    </template>

                    <v-divider class="my-2" />
                    <v-btn :loading="loading" :disabled="!tool.isValid" type="submit" color="primary" variant="tonal"
                      >Test Connection</v-btn
                    >
                    <v-divider class="my-2" v-if="testToolResult" />
                    <div v-if="testToolResult">
                      <v-card
                        v-for="content in testToolResult.content"
                        :key="content.type"
                        variant="outlined"
                        class="my-2"
                        color="primarys"
                        density="compact"
                      >
                        <v-card-text>
                          <json-viewer :key="tool.id" :value="JSON.parse(content.text)" theme="light" />
                        </v-card-text>
                      </v-card>
                    </div>
                  </v-form>
                </v-expansion-panel-text>
              </v-expansion-panel>
            </v-expansion-panels>
          </v-window-item>
        </v-window>
        <!-- </v-card-text> -->
      </v-card>
    </v-col>
    <v-col cols="4">
      <v-card
        :prepend-avatar="selectedTool.imageUrl"
        title="Connect"
        :text="'To create connection, Please test the tools under Tools Tab'"
        variant="flat"
      >
        <v-divider />
        <v-card-text>
          <tool-form />
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
  <DeleteDialog
    v-model:show="dialog"
    title="Warning!"
    :message="`Are you sure you want to remove this tool??`"
    @confirm="deleteConnection"
  />
</template>
<!-- <style >
@import 'md-editor-v3/lib/style.css';
</style> -->

<script setup lang="js">
import { mdiClose } from '@mdi/js';
import DeleteDialog from '@/components/shared/DeleteDialog.vue';
import { useToolStore } from '@/stores/apps/toolStore';
import { MdPreview } from 'md-editor-v3';
import { JsonViewer } from 'vue3-json-viewer';
import JsonEditorVue from 'json-editor-vue';
import { Mode } from 'vanilla-jsoneditor';

import ToolForm from './ToolForm.vue';

// import ToolDelete from './ToolDelete.vue';

import { onMounted, ref, toRefs } from 'vue';
import { mdiDelete } from '@mdi/js';
import { capitalize } from '@/utils/helpers/text';
const tab = ref('overview');
const dialog = ref(false);

const { selectedTool, loading, agentTools, toolId, selectedToolNames, selectedToolObjects } = toRefs(useToolStore());
const { testConnection, getFields, deleteConnection, fetchMcp, findToolById } = useToolStore();
onMounted(async () => {
  import('md-editor-v3/lib/style.css');
  import('vanilla-jsoneditor/themes/jse-theme-dark.css');
  await findToolById(toolId.value);
  await fetchMcp(toolId.value);
});
</script>
<template>
  <v-row>
    <v-col cols="8">
      <v-card :title="selectedTool.name" :prepend-avatar="selectedTool.imageUrl">
        <template #append v-if="agentTools.includes(selectedTool.id)">
          <v-btn color="error" variant="tonal" @click="dialog = true" :prepend-icon="mdiDelete" text="Disconnect"></v-btn>
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
            <v-row justify="center" class="pa-4">
              <v-col cols="12">
                <v-select
                  v-model="selectedToolNames"
                  :items="selectedTool?.tools.map((t) => t.name) || []"
                  label="Select Tools"
                  variant="outlined"
                  dense
                  multiple
                  clearable
                />
                <v-expansion-panels variant="accordion" class="mb-2" v-for="tool in selectedToolObjects" :key="tool.name" elevation="0">
                  <v-expansion-panel>
                    <v-expansion-panel-title>
                      {{ tool.name }}
                    </v-expansion-panel-title>

                    <v-expansion-panel-text>
                      <div class="my-3">
                        <v-label class="mb-2 text-subtitle-1">Used When</v-label>
                        <br />
                        <span class="text-subtitle-2 text-grey"
                          >Provide a detailed description explaining when this tool is used. Include examples of the data to use this tools
                          with user query, it will help LLM to understand</span
                        >
                        <v-textarea v-model="tool.description" auto-grow variant="outlined" rows="4" class="mb-4" />
                      </div>
                      <v-divider class="mb-4" />
                      <v-form @submit.prevent="testConnection(tool)" v-model="tool.isValid">
                        <v-row>
                          <template v-for="field in getFields(tool)" :key="field.name">
                            <v-col cols="12">
                              <template v-if="field.type === 'number'">
                                <v-label class="mb-2 text-subtitle-1">{{ capitalize(field.title) }}</v-label>
                                <v-text-field
                                  variant="outlined"
                                  v-model.number="tool.formData[field.name]"
                                  :placeholder="field.title"
                                  type="number"
                                  :rules="[field.required ? (v) => !!v || 'Required' : () => true]"
                                />
                              </template>
                              <div v-else-if="field.type === 'object'">
                                <v-label class="mb-4 text-subtitle-1">{{ capitalize(field.title) }}</v-label>
                                <JsonEditorVue
                                  :model-value="tool.formData[field.name]"
                                  @update:model-value="(value) => (tool.formData[field.name] = JSON.parse(value))"
                                  class="jse-theme-light"
                                  :mode="Mode.text"
                                  :rules="[field.required ? (v) => !!v || 'Required' : () => true]"
                                />
                              </div>
                              <v-checkbox
                                v-else-if="field.type === 'boolean'"
                                v-model="tool.formData[field.name]"
                                :label="capitalize(field.title)"
                                :rules="[field.required ? (v) => v !== null || 'Required' : () => true]"
                              />
                              <template v-else-if="field.type === 'array'">
                                <div class="d-flex justify-space-between align-center mb-2">
                                  <span class="text-subtitle-1 mb-2">{{ capitalize(field.title) }}</span>
                                  <v-btn
                                    small
                                    color="black"
                                    variant="flat"
                                    size="small"
                                    @click="
                                      tool.formData[field.name] = tool.formData[field.name] || [];
                                      tool.formData[field.name].push('');
                                    "
                                  >
                                    Add
                                  </v-btn>
                                </div>
                                <v-text-field
                                  v-for="(_, index) in tool.formData[field.name]"
                                  :key="index"
                                  v-model="tool.formData[field.name][index]"
                                  :placeholder="`${field.title} #${index + 1}`"
                                  variant="outlined"
                                  class="mb-2"
                                  :rules="[field.required ? (v) => !!v || 'Required' : () => true]"
                                  :append-inner-icon="mdiClose"
                                  @click:append-inner="tool.formData[field.name].splice(index, 1)"
                                />
                              </template>
                              <template v-else>
                                <v-label class="mb-2 text-subtitle-1">{{ capitalize(field.title) }}</v-label>
                                <v-text-field
                                  variant="outlined"
                                  v-model="tool.formData[field.name]"
                                  :placeholder="field.title"
                                  type="text"
                                  :rules="[field.required ? (v) => !!v || 'Required' : () => true]"
                                />
                              </template>
                            </v-col>
                          </template>
                        </v-row>

                        <v-divider class="my-4" />

                        <v-btn :loading="loading" :disabled="!tool.isValid" type="submit" color="primary" variant="elevated" block>
                          Try
                        </v-btn>

                        <v-divider class="my-4" v-if="tool.testResult" />

                        <div v-if="tool.testResult">
                          <v-expansion-panels multiple variant="popout" elevation="0">
                            <v-expansion-panel v-for="content in tool.testResult.content" :key="content.type" class="my-2">
                              <v-expansion-panel-title>
                                {{ content.type }}
                              </v-expansion-panel-title>
                              <v-expansion-panel-text>
                                <json-viewer :value="content" theme="light" />
                              </v-expansion-panel-text>
                            </v-expansion-panel>
                          </v-expansion-panels>
                        </div>
                      </v-form>
                    </v-expansion-panel-text>
                  </v-expansion-panel>
                </v-expansion-panels>
              </v-col>
            </v-row>
          </v-window-item>
        </v-window>
      </v-card>
    </v-col>
    <v-col cols="4">
      <v-card title="Credentials" :text="'Important: Test the connection in the Tools tab before saving your credentials.'" variant="flat">
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
    :message="`Are you sure you want to disconnect to this tool??`"
    @confirm="deleteConnection"
  />
</template>
<!-- <style >
@import 'md-editor-v3/lib/style.css';
</style> -->

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

const { selectedTool, loading, agentTools, toolId, selectedToolNames, selectedToolObjects } = toRefs(useToolStore());
const { testConnection, getFields, deleteConnection, fetchMcp, findToolById } = useToolStore();
onMounted(async () => {
  import('md-editor-v3/lib/style.css');
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
                      <div class="text-subtitle-1 text-grey my-3">
                        <!-- <MdPreview :model-value="tool.description" preview-theme="github" language="en" /> -->
                        <v-textarea v-model="tool.description" label="Used When" auto-grow variant="outlined" rows="4" class="mb-4" />
                      </div>

                      <v-form @submit.prevent="testConnection(tool)" v-model="tool.isValid">
                        <v-row>
                          <template v-for="field in getFields(tool)" :key="field.name">
                            <v-col cols="12" md="6">
                              <v-text-field
                                v-if="field.type === 'number'"
                                variant="outlined"
                                v-model.number="tool.formData[field.name]"
                                :placeholder="field.title"
                                type="number"
                                :rules="[field.required ? (v) => !!v || 'Required' : () => true]"
                              />
                              <v-text-field
                                v-else
                                variant="outlined"
                                v-model="tool.formData[field.name]"
                                :placeholder="field.title"
                                type="text"
                                :rules="[field.required ? (v) => !!v || 'Required' : () => true]"
                              />
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

                <!-- <template v-for="tool in selectedToolObjects" :key="tool.name">
                  <v-card variant="outlined" class="pa-4 mb-4">
                    <v-card-title class="text-h6">
                      {{ tool.name }}
                    </v-card-title>

                    <v-card-text>
                      <div class="text-subtitle-1 text-grey my-3">
                        <v-textarea v-model="tool.description" label="Used When" auto-grow variant="outlined" rows="4" class="mb-4" />
                      </div>

                      <v-form @submit.prevent="testConnection(tool)" v-model="tool.isValid">
                        <v-row>
                          <template v-for="field in getFields(tool)" :key="field.name">
                            <v-col cols="12" md="6">
                              <v-text-field
                                v-if="field.type === 'number'"
                                variant="outlined"
                                v-model.number="tool.formData[field.name]"
                                :placeholder="field.title"
                                type="number"
                                :rules="[field.required ? (v) => !!v || 'Required' : () => true]"
                              />
                              <v-text-field
                                v-else
                                variant="outlined"
                                v-model="tool.formData[field.name]"
                                :placeholder="field.title"
                                type="text"
                                :rules="[field.required ? (v) => !!v || 'Required' : () => true]"
                              />
                            </v-col>
                          </template>
                        </v-row>

                        <v-divider class="my-4" />

                        <v-btn :loading="loading" :disabled="!tool.isValid" type="submit" color="primary" variant="elevated" block>
                          Try
                        </v-btn>

                        <v-divider class="my-4" v-if="tool.testResult" />

                        <div v-if="tool.testResult">
                          <v-card
                            v-for="content in tool.testResult.content"
                            :key="content.type"
                            outlined
                            class="my-2"
                            color="primarys"
                            density="compact"
                          >
                            <v-card-text>
                              <json-viewer :value="content" theme="light" />
                            </v-card-text>
                          </v-card>
                        </div>
                      </v-form>
                    </v-card-text>
                  </v-card>
                </template> -->
              </v-col>
            </v-row>

            <!-- <v-expansion-panels variant="accordion">
              <v-expansion-panel v-for="tool in selectedTool.tools" :key="tool.name">
                <v-expansion-panel-title>
                  {{ tool.name }}
                </v-expansion-panel-title>
                <v-expansion-panel-text>
                  <div class="text-subtitle-1 text-grey my-3">
                     
                       <MdPreview :model-value="tool.description" preview-theme="github" language="en"></MdPreview>
                  </div>
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
                    <v-divider class="my-2" v-if="tool.testResult" />
                    <div v-if="tool.testResult">
                      <v-card
                        v-for="content in tool.testResult.content"
                        :key="content.type"
                        variant="outlined"
                        class="my-2"
                        color="primarys"
                        density="compact"
                      >
                        <v-card-text>
                          <json-viewer :key="tool.id" :value="content" theme="light" />
                        </v-card-text>
                      </v-card>
                    </div>
                  </v-form>
                </v-expansion-panel-text>
              </v-expansion-panel>
            </v-expansion-panels> -->
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
    :message="`Are you sure you want to disconnect to this tool??`"
    @confirm="deleteConnection"
  />
</template>
<!-- <style >
@import 'md-editor-v3/lib/style.css';
</style> -->

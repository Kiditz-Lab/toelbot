<template>
  <div>
    <BaseBreadcrumb title="Settings" :breadcrumbs="breadcrumbs"></BaseBreadcrumb>
    <v-row>
      <v-col cols="12" md="8">
        <agent-config />
      </v-col>
      <v-col cols="12" md="4">
        <v-row>
          <v-col cols="12">
            <UiParentCard title="Basic">
              <UpdateAgent />
            </UiParentCard>
          </v-col>
          <v-col cols="12">
            <v-card variant="outlined" elevation="0" class="bg-surface">
              <v-card-item class="py-3 px-5">
                <div class="d-sm-flex align-center justify-space-between">
                  <v-card-title class="text-h5" style="line-height: 1.57">
                    <v-avatar size="35" color="error">
                      <DeleteOutlined />
                    </v-avatar>
                    <span class="mx-2">Delete Agent</span>
                  </v-card-title>
                </div>
              </v-card-item>
              <v-divider />

              <v-card-text>
                Deleting agent is a permanent action that cannot be reversed. Deleting the agent will delete all documents indexed against
                it and all history.
              </v-card-text>
              <v-divider></v-divider>
              <v-card-actions class="justify-end">
                <v-btn @click="dialog = true" color="error" text="Delete" variant="tonal">Delete</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
    <DeleteDialog v-model:show="dialog" title="Warning!" message="Are you sure you want to delete the bot?" @confirm="confirmDelete" />
  </div>
</template>

<script setup lang="ts">
import { DeleteOutlined } from '@ant-design/icons-vue';
import BaseBreadcrumb from '@/components/shared/BaseBreadcrumb.vue';
import DeleteDialog from '@/components/shared/DeleteDialog.vue';
import UiParentCard from '@/components/shared/UiParentCard.vue';
import UpdateAgent from '@/components/apps/agents/UpdateAgent.vue';
import AgentConfig from '@/components/apps/agents/AgentConfig.vue';
import { onMounted, ref } from 'vue';
import { useAgentStore } from '@/stores/apps/agentStore';

const dialog = ref(false);
const { deleteAgent, fetchAgent } = useAgentStore();

const breadcrumbs = ref([{ title: 'Settings', disabled: false, href: '#' }]);
const confirmDelete = async () => {
  dialog.value = false;
  await deleteAgent();
};
onMounted(fetchAgent);
</script>

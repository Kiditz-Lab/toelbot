<script setup lang="ts">
import { useToolStore } from '@/stores/apps/toolStore';
import { mdiCheckCircle, mdiOpenInNew } from '@mdi/js';
import { toRefs } from 'vue';

const { tools, agentTools } = toRefs(useToolStore());
console.log('Agents', agentTools.value);
console.log(tools.value.map((tool) => tool.id));
const { selectTool } = useToolStore();
</script>
<template>
  <v-row justify="start">
    <v-col v-for="tool in tools" :key="tool.id" cols="12" sm="6" md="4" lg="4">
      <v-card class="mx-auto" :prepend-avatar="tool.imageUrl" :title="tool.name" density="compact">
        <v-card-text class="line-clamp-3 pa-0 px-3 py-1">
          {{ tool.shortDescription }}
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
          <!-- <v-spacer></v-spacer> -->
          <v-btn
          block
            v-if="!agentTools.includes(tool.id)"
            :prepend-icon="mdiOpenInNew"
            text="Connect"
            variant="outlined"
            color="secondary"
            @click="selectTool(tool)"
          ></v-btn>
          <v-btn
            v-else
            :prepend-icon="mdiCheckCircle"
            text="Connected"
            variant="flat"
            color="success"
            block
            @click="selectTool(tool)"
          ></v-btn>
        </v-card-actions>
      </v-card>
    </v-col>
  </v-row>
</template>
<style lang="css" scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

<script setup lang="ts">
import { mdiCheckCircle } from '@mdi/js';
import { useSnackbarStore } from '@/stores/snackbarStore';
import { toRefs } from 'vue';

const { timeout, message, show, color, header } = toRefs(useSnackbarStore());
const { hideSnackbar } = useSnackbarStore();
</script>

<template>
  <v-snackbar v-model="show" :timeout="timeout" :color="color" variant="flat">
    <v-row align="center">
      <v-col cols="auto">
        <v-icon :icon="mdiCheckCircle" color="white"></v-icon>
      </v-col>
      <v-col>
        <span class="headline">{{ header }}</span>
        <div>{{ message }}</div>
      </v-col>
    </v-row>

    <template v-slot:actions>
      <v-btn color="white" variant="tonal" @click="hideSnackbar()">Close</v-btn>
    </template>
  </v-snackbar>
</template>

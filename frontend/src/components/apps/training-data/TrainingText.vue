<script setup lang="ts">
import { useTrainingTextStore } from '@/stores/apps/trainingTextStore';
import { toRefs } from 'vue';
const { text } = toRefs(useTrainingTextStore());
</script>
<template>
  <v-textarea placeholder="Type your text..." v-model="text" type="text" name="name" auto-grow variant="outlined" rows="20"></v-textarea>
</template>

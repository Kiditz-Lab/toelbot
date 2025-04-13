<script setup lang="ts">
import { useTextDiff } from '@/stores/apps/textDiff';
import { ref } from 'vue';

const originalText = ref('');
const changedText = ref('');


const diffResult = useTextDiff(originalText, changedText);
</script>

<template>
  <v-card variant="outlined">
    <v-card-item>
      <v-card-title>Text Diff</v-card-title>
      <v-row class="mt-3">
        <v-col cols="6">
          <v-textarea v-model="originalText" label="Original Text" auto-grow variant="outlined" rows="15"></v-textarea>
        </v-col>
        <v-col cols="6">
          <v-textarea v-model="changedText" label="Changed Text" auto-grow variant="outlined" rows="15"></v-textarea>
        </v-col>
      </v-row>

      <v-row class="mt-4" v-if="originalText.length > 0 || changedText.length > 0">
        <v-col cols="6">
          <v-card elevation="0" variant="text">
            <v-card-title class="text-h6">Original</v-card-title>
            <v-card-text>
              <pre v-html="diffResult.left"></pre>
            </v-card-text>
          </v-card>
        </v-col>
        <v-col cols="6">
          <v-card elevation="0" variant="text">
            <v-card-title class="text-h6">Changed</v-card-title>
            <v-card-text>
              <pre v-html="diffResult.right"></pre>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-card-item>
  </v-card>
</template>

<style>
.diff-container {
  font-family: monospace;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.diff-removed {
  background-color: #ffebe9;
  color: #b00020;
  text-decoration: line-through;
  padding: 1px;
}

.diff-added {
  background-color: #d4fcbc;
  color: #006400;
  padding: 1px;
}

.diff-unchanged {
  color: #333;
  padding: 1px;
}
</style>

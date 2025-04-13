<template>
  <!-- <v-container> -->
  <!-- Drag and Drop Zone -->
  <v-sheet
    class="drop-zone"
    :class="{ dragover: isDragging }"
    @dragover.prevent="onDragOver"
    @dragleave.prevent="onDragLeave"
    @drop.prevent="onDrop"
    @click="triggerFileInput"
  >
    <v-icon size="40" color="primary" :icon="mdiCloudUpload"></v-icon>
    <div class="text-h6 mt-2">Drag & Drop files here or click to upload</div>
    <div class="text-caption grey--text mt-1">Supported: .pdf, .doc, .docx, .txt</div>
  </v-sheet>

  <!-- Hidden File Input -->
  <v-file-input ref="fileInput" multiple accept=".pdf,.doc,.docx,.txt,.md" class="d-none" @change="onFileChange"></v-file-input>
</template>

<script setup>
import { ref, watch } from 'vue';
import { mdiCloudUpload } from '@mdi/js';

const emit = defineEmits(['upload']);
const files = ref([]);
const isDragging = ref(false);
const fileInput = ref(null);

// Watch for file changes and emit event
watch(
  files,
  (newFiles) => {
    emit('upload', newFiles);
  },
  { deep: true }
);

// Trigger file input
const triggerFileInput = () => {
  fileInput.value.$el.querySelector('input').click();
};

// Handles file selection via file input
const onFileChange = (event) => {
  if (!event || !event.target || !event.target.files) return;

  const selectedFiles = Array.from(event.target.files);

  // Ensure Vue detects the change by creating a new array
  files.value = [...selectedFiles];

  // Reset the file input to allow re-upload of the same file
  event.target.value = null;
};

// Handles drag-over styling
const onDragOver = (event) => {
  isDragging.value = true;
  event.dataTransfer.dropEffect = 'copy';
};

// Removes drag-over styling
const onDragLeave = () => {
  isDragging.value = false;
};

// Handles file drop
const onDrop = (event) => {
  isDragging.value = false;
  const droppedFiles = Array.from(event.dataTransfer.files).filter((file) =>
    [
      'application/pdf',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'text/plain',
      'text/markdown'
    ].includes(file.type)
  );

  // Ensure Vue detects the change
  files.value = [...droppedFiles];
};


</script>

<style scoped>
.drop-zone {
  border: 2px dashed #1976d2;
  border-radius: 8px;
  padding: 4rem;
  text-align: center;
  
  cursor: pointer;
  transition: all 0.3s ease;
}

.drop-zone.dragover {
  border-color: #4caf50;
  background-color: rgba(25, 118, 210, 0.1);
}
</style>

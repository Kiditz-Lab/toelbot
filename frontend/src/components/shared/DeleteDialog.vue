<!-- src/components/ConfirmationDialog.vue -->
<template>
  <v-dialog v-model="dialog" width="auto">
    <v-card max-width="400" rounded="xl">
      <v-card-title class="text-error text-center pt-4">{{ props.title }}</v-card-title>
      <v-card-text class="text-center message-text">{{ props.message }}</v-card-text>

      <v-card-actions>
        <v-btn text @click="closeDialog">No</v-btn>
        <v-btn color="error" text @click="confirmDeletion">Yes</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue';

// Define props for customizable title and message
const props = defineProps({
  title: {
    type: String,
    required: true
  },
  message: {
    type: String,
    required: true
  },
  show: { type: Boolean, required: true }
});

// Emit events to the parent
const emit = defineEmits(['update:show', 'confirm']);

// Control dialog visibility
const dialog = ref(false);

// Close the dialog and emit 'cancel' event
const closeDialog = () => {
  dialog.value = false;
  
};

// Confirm deletion, close the dialog, and emit 'confirm' event
const confirmDeletion = () => {
  emit('confirm');
  
};

watch(dialog, (newVal) => {
  emit('update:show', newVal);
});

// // Sync dialog with parent’s show prop
watch(
  () => props.show,
  (newVal) => {
    dialog.value = newVal;
  }
);
</script>

<style lang="css" scoped>
.message-text {
  white-space: pre-line;
}
</style>

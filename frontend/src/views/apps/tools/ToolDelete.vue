<template>
  <v-card elevation="0">
    <v-card-title>Remove Connection</v-card-title>
    <v-card-text>You can easily remove connection to the agent tools by removing them.</v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn color="error" text="Remove Connection" :prepend-icon="mdiDelete" variant="flat" block>
        
      </v-btn>
    </v-card-actions>
  </v-card>
</template>
<script setup lang="ts">
import { mdiDelete } from "@mdi/js";
</script>
<!-- AgentForm.vue -->
<template>
  <v-dialog v-model="dialog" max-width="400">
    <v-card rounded="xl">
      <v-card-title class="text-h5 text-center"> Create a new agent </v-card-title>
      <v-card-text class="px-8 py-0">
        <v-form ref="formRef" @submit.prevent="save">
          <v-label class="font-weight-regular">Give your agent a name to easily identify it</v-label>
          <v-text-field
            :rules="nameRules"
            v-model="form.name"
            hide-details="auto"
            variant="outlined"
            class="mt-2"
            placeholder="Enter your agent name"
          />
          <v-switch v-model="form.isPublic" label="Make It Public" color="primary" class="mt-3" />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn class="pa-2" color="grey" variant="text" @click="dialog = false">Close</v-btn>
        <v-btn color="primary" variant="tonal" @click="save">Let's Go</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { useApi } from '@/composables/useApi';
import type { Agent } from '@/types/agent';
import type { ValidationRule } from '@/types/validation';
import { ref, watch } from 'vue';
const formRef = ref<HTMLFormElement | null>(null);
const props = defineProps({
  show: { type: Boolean, required: true }
});
const dialog = ref(false);
const form = ref({ name: '', isPublic: false });
const emit = defineEmits(['update:show', 'save']);
const api = useApi();

// Update parent when dialog changes (for v-model)
watch(dialog, (newVal) => {
  emit('update:show', newVal);
});

// // Sync dialog with parent’s show prop
watch(
  () => props.show,
  (newVal) => {
    dialog.value = newVal;
    if (!newVal) resetForm();
  }
);

const nameRules = ref<ValidationRule[]>([
  (v: string) => !!v || 'Name is required',
  (v: string) => (v && v.length <= 20) || 'Name must be 20 characters or less'
]);

function resetForm() {
  formRef?.value?.reset();
}

const save = async () => {
  if (formRef.value) {
    const { valid } = await formRef.value.validate();
    if (!valid) return;
    const agent = await api.post<Agent>('/agents', form.value)
    dialog.value = false;
    emit('save', agent);
  }
};
// import { ref, watch } from 'vue';
// import { useForm, useField } from 'vee-validate';
// import * as yup from 'yup'; // Use Yup for schema validation
// // Define props and emits
// const props = defineProps({
//   show: { type: Boolean, required: true }
// });
// const emit = defineEmits(['update:show', 'save']);

// const dialog = ref(false); // Local dialog state

// // Validation schema with Yup
// const schema = yup.object({
//   agentName: yup.string().required('Agent name is required').nullable(false)
// });

// // Set up VeeValidate form
// const { handleSubmit, resetForm } = useForm({
//   validationSchema: schema
// });

// // Set up field
// const { value: formAgentName, errors: agentNameErrors } = useField('agentName');

// // Reactive form object for v-model
// const form = ref({
//   agentName: ''
// });

// // Sync form.agentName with VeeValidate’s field value
// watch(formAgentName, (newVal) => {
//   form.value.agentName = newVal;
// });
// watch(
//   () => form.value.agentName,
//   (newVal) => {
//     formAgentName.value = newVal;
//   }
// );

// // Update parent when dialog changes (for v-model)
// watch(dialog, (newVal) => {
//   emit('update:show', newVal);
// });

// const save = handleSubmit((values) => {
//     emit('save', values.agentName);

// });
</script>
<style scoped>
.round {
  border-radius: 30px;
}
</style>

import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useSnackbarStore = defineStore('snackbar', () => {
  const show = ref(false);
  const message = ref('');
  const header = ref('');
  const color = ref('info');
  const timeout = ref(300);

  function showSnackbar(msg: string, clr = 'info', time = 3000, head = 'Success') {
    message.value = msg;
    color.value = clr;
    timeout.value = time;
    header.value = head;
    show.value = true;
  }

  function hideSnackbar() {
    show.value = false;
  }

  return {
    show,
    message,
    color,
    timeout,
    header,
    showSnackbar,
    hideSnackbar,
  };
});

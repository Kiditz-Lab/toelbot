<template>
    <div>User: {{ user ? user.email : "Not authenticated" }}</div>
    <div>Token: {{ token }}</div>
  </template>
  
  <script setup>
  import { auth } from "@/utils/firebase";
  import { ref, onMounted } from "vue";
  import { onAuthStateChanged } from "firebase/auth";
  
  const user = ref(null);
  const token = ref(null);
  
  onMounted(() => {
    onAuthStateChanged(auth, async (currentUser) => {
      if (currentUser) {
        user.value = currentUser;
        try {
          token.value = await currentUser.getIdToken();
        } catch (error) {
          console.error("Error fetching token:", error.message);
          token.value = "Error fetching token";
        }
      } else {
        token.value = "User is not authenticated";
      }
    });
  });
  </script>
  
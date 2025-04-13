<script setup lang="ts">
import { useCustomizerStore } from '../../../stores/customizer';
// icons
import { MenuFoldOutlined } from '@ant-design/icons-vue';
const { user } = useAuthStore();
// dropdown imports
// import Searchbar from './SearchBarPanel.vue';
import ProfileDD from './ProfileDD.vue';
// import CategoryMenu from './CategoryMenu.vue';
import { useAuthStore } from '@/stores/auth';
const customizer = useCustomizerStore();
defineProps({
  isLogo: {
    type: Boolean,
    default: false
  }
});
</script>

<template>
  <v-app-bar elevation="0">
    <v-btn v-if="!isLogo"
      class="hidden-md-and-down text-secondary mx-3"
      color="darkText"
      icon
      rounded="sm"
      variant="text"
      @click.stop="customizer.SET_MINI_SIDEBAR(!customizer.mini_sidebar)"
      size="small"
    >
      <!-- <img  src="@/assets/logo.svg" width="38px" height="38px" class="mx-3" /> -->

      <MenuFoldOutlined :style="{ fontSize: '16px' }" />
    </v-btn>
    <div v-else class="d-flex align-center">
      <img src="@/assets/logo.svg" width="42px" height="42px" class="mx-3" />
      <v-toolbar-title class="font-weight-bold text-h3">Toelbot</v-toolbar-title>
    </div>
    <v-btn v-if="!isLogo"
      class="hidden-lg-and-up text-secondary ms-3"
      color="darkText"
      icon
      rounded="sm"
      variant="text"
      @click.stop="customizer.SET_SIDEBAR_DRAWER"
      size="small"
    >
      <MenuFoldOutlined :style="{ fontSize: '16px' }" />
    </v-btn>

    <!-- search mobile -->
    <!-- <v-menu :close-on-content-click="false" class="hidden-lg-and-up" offset="10, 0">
      <template v-slot:activator="{ props }">
        <v-btn
          class="hidden-lg-and-up text-secondary ml-1"
          color="lightsecondary"
          icon
          rounded="sm"
          variant="flat"
          size="small"
          v-bind="props"
        >
          <SearchOutlined :style="{ fontSize: '17px' }" />
        </v-btn>
      </template>
      <v-sheet class="search-sheet v-col-12 pa-0" width="320">
        <v-text-field persistent-placeholder placeholder="Search here.." color="primary" variant="solo" hide-details>
          <template v-slot:prepend-inner>
            <SearchOutlined :style="{ fontSize: '17px' }" />
          </template>
        </v-text-field>
      </v-sheet>
    </v-menu> -->

    <!-- ---------------------------------------------- -->
    <!-- Search part -->
    <!-- ---------------------------------------------- -->
    <!-- <v-sheet class="d-none d-lg-block" width="350">
      <Searchbar />
    </v-sheet> -->

    <!---/Search part -->

    <!-- ---------------------------------------------- -->
    <!-- Category -->
    <!-- ---------------------------------------------- -->
    <!-- <CategoryMenu /> -->

    <!---/Category -->

    <v-spacer />
    <!-- ---------------------------------------------- -->
    <!---right part -->
    <!-- ---------------------------------------------- -->

    <!-- ---------------------------------------------- -->
    <!-- Github -->
    <!-- ---------------------------------------------- -->
    <!-- <v-btn
      icon
      class="text-secondary hidden-sm-and-down d-flex"
      color="darkText"
      rounded="sm"
      variant="text"
      href="https://github.com/codedthemes/mantis-free-vuetify-vuejs-admin-template"
      target="_blank"
    >
      <GithubOutlined :style="{ fontSize: '16px' }" />
    </v-btn> -->

    <!-- ---------------------------------------------- -->
    <!-- Notification -->
    <!-- ---------------------------------------------- -->
    <!-- <NotificationDD /> -->

    <!-- ---------------------------------------------- -->
    <!-- User Profile -->
    <!-- ---------------------------------------------- -->
    <v-menu :close-on-content-click="false" offset="8, 0" v-if="user">
      <template v-slot:activator="{ props }">
        <v-btn class="profileBtn" variant="text" rounded="sm" v-bind="props">
          <div class="d-flex align-center">
            <v-avatar class="mr-sm-2 mr-0 py-2">
              <img :src="user.photoURL" :alt="user.displayName" referrerPolicy="no-referrer" />
            </v-avatar>
            <h6 class="text-subtitle-1 mb-0 d-sm-block d-none">{{ user.displayName }}</h6>
          </div>
        </v-btn>
      </template>
      <v-sheet rounded="md" width="290">
        <ProfileDD />
      </v-sheet>
    </v-menu>
    <v-btn href="/login" color="primary " variant="tonal" rounded="lg" class="mr-sm-2 mr-0 py-2" v-else> Sign In </v-btn>
  </v-app-bar>
</template>

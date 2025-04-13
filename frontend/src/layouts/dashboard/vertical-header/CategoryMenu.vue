<script lang="ts" setup>
import { DownOutlined } from "@ant-design/icons-vue";
import { shallowRef } from "vue";
import sidebarItems from '@/layouts/dashboard/vertical-sidebar/sidebarItem';
import NavCollapse from '@/layouts/dashboard/vertical-sidebar/NavCollapse/NavCollapse.vue';
import NavGroup from '@/layouts/dashboard/vertical-sidebar/NavGroup/NavGroup.vue';
import NavItem from '@/layouts/dashboard/vertical-sidebar/NavItem/NavItem.vue';

const sidebarMenu = shallowRef(sidebarItems);
</script>
<template>
    <v-menu open-on-hover>
      <template v-slot:activator="{ props }" >
        <v-btn color="secondary" v-bind="props" class="ml-5">
          All Tools
          <template v-slot:append>
            <DownOutlined />
          </template>
        </v-btn>
      </template>
      <v-list aria-busy="true" aria-label="menu list">
        <template v-for="(item, i) in sidebarMenu" :key="i">
          <!---Item Sub Header -->
          <NavGroup :item="item" v-if="item.header" :key="item.title" />
          <!---Item Divider -->
          <v-divider class="my-3" v-else-if="item.divider" />
          <!---If Has Child -->
          <NavCollapse class="leftPadding" :item="item" :level="0" v-else-if="item.children" />
          <!---Single Item-->
          <NavItem :item="item" v-else />
          <!---End Single Item-->
        </template>
        <!-- <v-list-item>
          <template v-slot:prepend>
            <FontSizeOutlined :style="{fontSize: '14px'}" class="mr-4 text-primary" />
          </template>
          <v-list-item-title>Text Tools</v-list-item-title>
        </v-list-item>
        <v-list-item>
          <template v-slot:prepend>
            <BgColorsOutlined :style="{fontSize: '14px'}" class="mr-4 text-primary" />
          </template>
          <v-list-item-title>Color Tools</v-list-item-title>
        </v-list-item>
        <v-list-item>
          <template v-slot:prepend>
            <FileImageOutlined :style="{fontSize: '14px'}" class="mr-4 text-primary" />
          </template>
          <v-list-item-title>Image Tools</v-list-item-title>
        </v-list-item>-->
      </v-list>
    </v-menu>
</template>
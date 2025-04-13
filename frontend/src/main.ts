import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import { router } from './router';
import vuetify from './plugins/vuetify';
import '@/scss/style.scss';
import "vue3-json-viewer/dist/index.css";
import { PerfectScrollbarPlugin } from 'vue3-perfect-scrollbar';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

import VueTablerIcons from 'vue-tabler-icons';
import VueApexCharts from 'vue3-apexcharts';
import Antd from 'ant-design-vue';
import '@mdi/font/css/materialdesignicons.css'

import 'ant-design-vue/dist/reset.css';

// google-fonts
import '@fontsource/public-sans/400.css';
import '@fontsource/public-sans/500.css';
import '@fontsource/public-sans/600.css';
import '@fontsource/public-sans/700.css';
//Mock Api data
import { fakeBackend } from '@/utils/helpers/fake-backend';
const app = createApp(App);
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate);
fakeBackend();
app.use(router);
app.use(PerfectScrollbarPlugin);
app.use(pinia);
app.use(VueTablerIcons);
app.use(Antd);
app.use(VueApexCharts);
app.use(vuetify).mount('#app');

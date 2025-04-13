<template>
  <div class="company-icons-container text-center">
    <!-- Left to Right Scroller -->
    <h2 class="text-h2 font-weight-bold mb-4">Plug & Play Tools</h2>
    <p class="text-h6 text-grey mb-8">
      Instantly integrate with 100+ tool. Toelbot connects with the tools you use, so you can automate tasks with ease.
    </p>

    <div class="scroller-section">
      
      <div class="scroller left-to-right">
        <div class="scroller-inner">
          <v-card
            v-for="(image, index) in leftToRightImages"
            :key="`ltr-${index}`"
            class="company-card mx-2"
            elevation="0"
            rounded="lg"
            color="grey-lighten-4"
          >
            <img :src="image" height="60" width="60" contain class="pa-2" />
          </v-card>
        </div>
      </div>
    </div>

    <!-- Right to Left Scroller -->
    <div class="scroller-section mt-8">
    
      <div class="scroller right-to-left">
        <div class="scroller-inner">
          <v-card
            v-for="(image, index) in rightToLeftImages"
            :key="`rtl-${index}`"
            class="company-card mx-2"
            elevation="0"
            rounded="lg"
            color="grey-lighten-4"
          >
            <img :src="image" height="60" width="60" contain class="pa-2" />
          </v-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

// Using import.meta.glob to get all images
const images = import.meta.glob('@/assets/tools/*.svg', { eager: true });

const allImages = ref([]);
const leftToRightImages = ref([]);
const rightToLeftImages = ref([]);
const imagesPerRow = 60;

onMounted(() => {
  loadImages();
});

const loadImages = () => {
  // Extract the image URLs from the glob results
  allImages.value = Object.values(images).map((module) => module.default);

  // Limit to 40 images per row as requested
  leftToRightImages.value = allImages.value.slice(0, imagesPerRow);
  rightToLeftImages.value = allImages.value.slice(imagesPerRow, imagesPerRow * 2);

  // Duplicate images to create a seamless scrolling effect
//   leftToRightImages.value = [...leftToRightImages.value, ...leftToRightImages.value];
//   rightToLeftImages.value = [...rightToLeftImages.value, ...rightToLeftImages.value];
};
</script>

<style scoped>
.company-icons-container {
  width: 100%;
  overflow: hidden;
}

.scroller {
  position: relative;
  overflow: hidden;
  width: 100%;
  white-space: nowrap;
}

.scroller-inner {
  display: inline-block;
  white-space: nowrap;
  animation-duration: 60s;
  animation-timing-function: linear;
  animation-iteration-count: infinite;
}

.left-to-right .scroller-inner {
  animation-name: scrollLeft;
}

.right-to-left .scroller-inner {
  animation-name: scrollRight;
}

.company-card {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  background-color: white;
  transition: transform 0.3s ease;
}

.company-card:hover {
  transform: scale(1.05);
  /* box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); */
}

@keyframes scrollLeft {
  0% {
    transform: translateX(0%);
  }
  100% {
    transform: translateX(-100%);
  }
}

@keyframes scrollRight {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(0%);
  }
}
</style>

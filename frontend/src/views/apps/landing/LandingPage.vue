<script setup>
import { ref } from 'vue';
import ToolScroll from './ToolScroll.vue';
import {
  mdiRobot,
  mdiSend,
  mdiAccount,
  mdiTwitter,
  mdiLinkedin,
  mdiFacebook,
  mdiCheckCircle,
  mdiPuzzle,
  mdiOfficeBuildingCog,
  mdiChat,
  mdiLightningBolt,
  mdiTimerSand,
  mdiWeb,
  mdiWhatsapp,
  mdiFacebookMessenger,
  mdiSlack,
  mdiInstagram
} from '@mdi/js';

// Drawer & UI state
const drawer = ref(false);
const userInput = ref('');
const isTyping = ref(false);

// Messages
const messages = ref([
  { id: 1, type: 'user', text: 'What’s the status of order #123?' },
  {
    id: 2,
    type: 'bot',
    text: 'It’s been shipped — here’s the tracking.'
  },
  {
    id: 3,
    type: 'user',
    text: 'Create a new product called Chill T-shirt, $29'
  },
  {
    id: 4,
    type: 'bot',
    text: 'Product added successfully.'
  },
  {
    id: 5,
    type: 'user',
    text: 'Send that to my slack channel.'
  },
  {
    id: 6,
    type: 'bot',
    text: 'Done.'
  }
]);

// Stats
const stats = ref([
  {
    value: '95%',
    label: 'Lower support costs with automated conversations'
  },
  {
    value: '24/7',
    label: 'Always-on AI for global customer support'
  },
  {
    value: '95%',
    label: 'Satisfaction rate across sales and support interactions'
  },
  {
    value: '3 min',
    label: 'To plug and play with your favorite tools'
  }
]);

// Features
const features = ref([
  {
    icon: mdiRobot,
    title: 'Smart Conversations That Get Things Done',
    description: 'Ask about orders, create products, or send updates — Toelbot understands and acts instantly.'
  },
  {
    icon: mdiPuzzle,
    title: 'Plug-and-Play Tool Integrations',
    description: 'Connect Slack, WhatsApp, Odoo, Gmail, Figma and more — no technical setup needed.'
  },
  {
    icon: mdiOfficeBuildingCog,
    title: 'One Chatbot, All Departments',
    description: 'From customer support to inventory and invoicing — Toelbot automates everything.'
  },
  {
    icon: mdiChat,
    title: 'Actions Across Channels',
    description: 'Chat with customers on WhatsApp, IG DMs, Facebook, or Slack — Toelbot works everywhere.'
  },
  {
    icon: mdiLightningBolt,
    title: 'Natural Commands, Real Automation',
    description: 'Just type what you need — no workflows, forms, or extra steps.'
  },
  {
    icon: mdiTimerSand,
    title: 'Fast Setup, Powerful Automation',
    description: 'Get started in under 3 minutes and let Toelbot do the heavy lifting for your business.'
  }
]);

// Steps
const steps = ref([
  {
    title: 'Connect Your Data',
    description: 'Add any Website, DOCX,TXT, PDF,CSV files, or even YouTube videos securely into your chatbot in minutes.'
  },
  {
    title: 'Customize Your Bot',
    description: 'Tailor responses, branding, and conversation flows to match your business needs.'
  },
  {
    title: 'Go Live',
    description: 'Deploy on your website, mobile app, or social media channels with a single click.'
  }
]);

// Testimonials
const testimonials = ref([
  {
    quote: 'Implementing Toelbot increased our conversion rate by 35% and drastically reduced our customer service workload.',
    author: 'Sarah Johnson',
    company: 'E-commerce Director, FashionRetail'
  },
  {
    quote: 'Our customers love getting instant answers about their orders. Support tickets dropped by 65% in the first month.',
    author: 'Michael Chen',
    company: 'CTO, TechGadgets Inc.'
  }
]);

// Plans
const plans = ref([
  {
    name: 'Starter',
    price: '49',
    description: 'Perfect for small businesses',
    popular: false,
    features: ['Up to 1,000 conversations/month', 'Basic AI capabilities', 'Order status integration', 'Email support']
  },
  {
    name: 'Professional',
    price: '149',
    description: 'Ideal for growing companies',
    popular: true,
    features: [
      'Up to 10,000 conversations/month',
      'Advanced AI capabilities',
      'Full API integrations',
      'Priority support',
      'Analytics dashboard'
    ]
  },
  {
    name: 'Enterprise',
    price: '499',
    description: 'For large scale operations',
    popular: false,
    features: ['Unlimited conversations', 'Custom AI training', 'Dedicated account manager', 'SLA guarantees', 'White-label options']
  }
]);

const integrations = ref([
  {
    title: 'Website',
    icon: mdiWeb
    // description: 'Engage with website visitors in real-time, offer instant support, and drive conversions.'
  },
  {
    title: 'WhatsApp',
    icon: mdiWhatsapp
    // description: 'Reach your customers on WhatsApp with automated replies and smooth interactions.'
  },
  {
    title: 'Messenger',
    icon: mdiFacebookMessenger
    // description: ''
  },
  {
    title: 'Slack',
    icon: mdiSlack
    // description: ''
  },
  {
    title: 'Instagram',
    icon: mdiInstagram
    // description: 'Manage DMs, product recommendations, and support on Instagram directly.'
  }
]);

const getCardColor = (icon) => {
  switch (icon) {
    case mdiWeb:
      return 'blue lighten-4';
    case mdiWhatsapp:
      return 'green lighten-4';
    case mdiFacebookMessenger:
      return 'blue darken-1';
    case mdiSlack:
      return 'purple lighten-4';
    case mdiInstagram:
      return 'pink lighten-4';
    default:
      return 'grey lighten-4';
  }
};

const getTextColor = (icon) => {
  switch (icon) {
    case mdiWeb:
      return 'blue darken-2';
    case mdiWhatsapp:
      return 'green darken-2';
    case mdiFacebookMessenger:
      return 'blue darken-3';
    case mdiSlack:
      return 'purple darken-3';
    case mdiInstagram:
      return 'pink darken-3';
    default:
      return 'grey darken-2';
  }
};

const imageModules = import.meta.glob('@/assets/tools/*.{png,jpg,jpeg,svg}', {
  eager: true,
  import: 'default'
});
// const tools = ref(Object.values(imageModules));

// const midpoint = Math.ceil(tools.value.length / 2);
// const leftTools = computed(() => tools.value.slice(0, midpoint));
// const rightTools = computed(() => tools.value.slice(midpoint));

console.log(imageModules);

// Send message method
function sendMessage() {
  if (!userInput.value.trim()) return;

  messages.value.push({
    id: messages.value.length + 1,
    type: 'user',
    text: userInput.value
  });

  userInput.value = '';
  isTyping.value = true;

  setTimeout(() => {
    isTyping.value = false;
    messages.value.push({
      id: messages.value.length + 1,
      type: 'bot',
      text: "I've updated your shipping address for order #12345. The changes have been applied successfully. Your package will now be delivered to the new address. Is there anything else you need help with?"
    });
  }, 1500);
}
</script>

<template>
  <v-app-bar app color="white" elevation="0" variant="flat" height="60">
    <div class="d-flex align-center">
      <img src="@/assets/logo.svg" width="42px" height="42px" class="mx-3" />
      <v-toolbar-title class="font-weight-bold text-h3">Toelbot</v-toolbar-title>
    </div>
    <v-spacer></v-spacer>

    <!-- Desktop Navigation Menu -->
    <div class="d-none d-md-flex mr-3">
      <v-btn variant="text" href="#features" class="text-none">Features</v-btn>
      <v-btn variant="text" href="#usecase" class="text-none">Use Case</v-btn>
      <v-btn variant="text" href="#pricing" class="text-none">Pricing</v-btn>
      <!-- <v-btn variant="text" href="#faq" class="text-none">FAQ</v-btn> -->
      <v-btn to="/login" class="bg-indigo text-white ml-4" elevation="1">Login</v-btn>
    </div>

    <!-- Mobile Menu Button -->
    <div class="d-flex d-md-none align-center">
      <v-btn to="/login" class="bg-indigo text-white mr-2" elevation="1" small>Login</v-btn>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
    </div>
  </v-app-bar>
  <v-main>
    <!-- Hero Section -->
    <section id="hero" class="custom-gradient">
      <v-container>
        <v-row align="center" class="py-12">
          <v-col cols="12" md="7" class="text-white">
            <h1 class="text-h1 font-weight-bold mb-4">Turn Conversations into Actions</h1>
            <p class="text-36 mb-6">
              Let your AI Agents handle everything from customer support and sales to backend processes, with zero code and full
              flexibility. Connect tools, Ask Agent, and get more done — instantly
            </p>
            <div class="d-flex flex-column flex-sm-row">
              <v-btn x-large color="white" class="text-none text-indigo font-weight-bold mb-4 mb-sm-0 mr-sm-4">Start Free</v-btn>
              <v-btn x-large variant="outlined" color="white" class="text-none font-weight-bold">Watch Demo</v-btn>
            </div>
          </v-col>
          <v-col cols="12" md="5">
            <div class="pa-10">
              <v-card class="mx-auto pa-4" elevation="10" rounded="lg">
                <!-- Chat Demo Component -->
                <div>
                  <div class="d-flex align-center mb-4">
                    <v-avatar color="indigo" class="mr-3">
                      <v-icon color="white" :icon="mdiRobot"></v-icon>
                    </v-avatar>
                    <div>
                      <div class="font-weight-medium">Toelbot Assistant</div>
                      <div class="text-caption text-green">Online</div>
                    </div>
                  </div>

                  <div class="chat-container pa-2 mb-4">
                    <div v-for="(message, i) in messages" :key="i" class="chat-message mb-3">
                      <v-row :justify="message.type === 'user' ? 'end' : 'start'" no-gutters>
                        <v-col
                          cols="auto"
                          :class="message.type === 'user' ? 'bg-indigo-lighten-5' : 'bg-grey-lighten-5'"
                          class="rounded-lg pa-3"
                          style="max-width: 80%"
                        >
                          {{ message.text }}
                        </v-col>
                      </v-row>
                    </div>
                    <div v-if="isTyping" class="chat-message">
                      <v-row justify="start" no-gutters>
                        <v-col cols="auto" class="bg-grey-lighten-5 rounded-lg pa-3">
                          <v-progress-linear indeterminate color="indigo" height="2"></v-progress-linear>
                        </v-col>
                      </v-row>
                    </div>
                  </div>

                  <v-row no-gutters>
                    <v-col>
                      <v-text-field
                        v-model="userInput"
                        placeholder="Type your message..."
                        variant="outlined"
                        readonly
                        hide-details
                        @keyup.enter="sendMessage"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="auto">
                      <v-btn color="indigo" class="text-white ml-2" height="40" width="40" @click="sendMessage">
                        <v-icon :icon="mdiSend"></v-icon>
                      </v-btn>
                    </v-col>
                  </v-row>
                </div>
                <!-- End Chat Demo Component -->
              </v-card>
            </div>
          </v-col>
        </v-row>
      </v-container>
    </section>
    <!-- Stats Section -->
    <section class="bg-white py-8">
      <v-container>
        <v-row>
          <v-col v-for="(stat, i) in stats" :key="i" cols="6" md="3" class="text-center">
            <h3 class="text-h3 font-weight-bold text-indigo mb-1">{{ stat.value }}</h3>
            <div class="text-subtitle-1 text-grey">{{ stat.label }}</div>
          </v-col>
        </v-row>
      </v-container>
    </section>

    <!-- Integrations Section -->
    <section class="py-12 bg-grey-lighten-4">
      <v-container>
        <div class="text-center mb-12">
          <h2 class="text-h2 font-weight-bold mb-4">Be Where Your Customers Are</h2>
          <p class="text-h6 text-grey">
            Scale your customer interactions without the overhead. Our chatbot manages conversations across multiple platforms — so you
            don’t have to.
          </p>
        </div>

        <v-row justify="center">
          <v-col v-for="(integration, index) in integrations" :key="index" cols="12" sm="4" md="2">
            <v-card class="text-center rounded-lg" rounded outlined :color="getCardColor(integration.icon)">
              <v-icon :icon="integration.icon" size="64" class="mt-4"> </v-icon>
              <v-card-title class="headline" :style="{ color: getTextColor(integration.icon) }">
                {{ integration.title }}
              </v-card-title>
              <v-card-text :style="{ color: getTextColor(integration.icon) }">
                {{ integration.description }}
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </section>
    <!-- Tools Section -->
    <section class="py-12">
      <v-container>
        <ToolScroll />
      </v-container>
    </section>
    <!-- Features Section -->
    <section id="features" class="bg-grey-lighten-4 py-12">
      <v-container>
        <div class="text-center mb-12">
          <h2 class="text-h2 font-weight-bold mb-4">Smarter Chats. Instant Actions.</h2>
          <p class="text-h3 text-grey">
            Toelbot integrates with your tools and turns customer, sales, and support messages into real business actions — from Slack to
            sales to support.
          </p>
        </div>
        <v-row>
          <v-col v-for="feature in features" :key="feature.title" cols="12" sm="6" md="4">
            <v-card class="pa-5" elevation="2">
              <v-icon :icon="feature.icon" size="36" color="primary" class="mb-4" />
              <h3 class="text-h6 font-weight-bold mb-2">
                {{ feature.title }}
              </h3>
              <p class="text-body-2 text-grey-darken-1">
                {{ feature.description }}
              </p>
            </v-card>
          </v-col>
        </v-row>

        <!-- <v-row>
          <v-col v-for="(feature, i) in features" :key="i" cols="12" sm="6" lg="3">
            <v-card height="100%" class="d-flex flex-column">
              <v-card-text>
                <v-avatar color="indigo" size="56" class="mb-4">
                  <v-icon dark :icon="feature.icon">{{ feature.icon }}</v-icon>
                </v-avatar>
                <h2 class="font-weight-bold mb-2">{{ feature.title }}</h2>
                <p class="text-grey">{{ feature.description }}</p>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row> -->
      </v-container>
    </section>
    <!-- How It Works Section -->
    <section id="usecase" class="py-12">
      <v-container>
        <div class="text-center mb-12">
          <h2 class="text-h2 font-weight-bold mb-4">How It Works</h2>
          <p class="text-h6 text-grey">Simple integration, powerful results. Get up and running in minutes.</p>
        </div>
        <v-row>
          <v-col v-for="(step, i) in steps" :key="i" cols="12" md="4" class="text-center">
            <v-avatar color="indigo" size="64" class="mb-4">
              <span class="text-white text-h5">{{ i + 1 }}</span>
            </v-avatar>
            <h3 class="text-h5 font-weight-medium mb-2">{{ step.title }}</h3>
            <p class="text-grey">{{ step.description }}</p>
          </v-col>
        </v-row>
      </v-container>
    </section>
    <!-- Testimonials Section -->
    <section class="bg-grey-lighten-4 py-12">
      <v-container>
        <div class="text-center mb-8">
          <h2 class="text-h4 font-weight-bold mb-4">What Our Clients Say</h2>
        </div>

        <v-carousel hide-delimiter-background show-arrows-on-hover height="auto" interval="8000">
          <v-carousel-item v-for="(testimonial, i) in testimonials" :key="i">
            <v-card class="mx-auto ma-4 pa-4" max-width="800">
              <v-card-text class="text-h6 font-italic text-center">"{{ testimonial.quote }}"</v-card-text>
              <v-card-text class="text-center">
                <v-avatar class="bg-indigo mb-2">
                  <v-icon color="white" :icon="mdiAccount" />
                </v-avatar>
                <div class="font-weight-medium">{{ testimonial.author }}</div>
                <div class="text-caption grey--text">{{ testimonial.company }}</div>
              </v-card-text>
            </v-card>
          </v-carousel-item>
        </v-carousel>
      </v-container>
    </section>
    <!-- Pricing Section -->
    <section id="pricing" class="py-12">
      <v-container>
        <div class="text-center mb-12">
          <h2 class="text-h4 font-weight-bold mb-4">Simple, Transparent Pricing</h2>
          <p class="text-h6 grey--text">Choose the plan that works for your business</p>
        </div>

        <v-row justify="center">
          <v-col v-for="(plan, i) in plans" :key="i" cols="12" sm="6" lg="4">
            <v-card height="100%" class="d-flex flex-column" :elevation="plan.popular ? 10 : 2" va>
              <v-card-title class="text-h5 font-weight-bold">
                {{ plan.name }}
                <v-chip v-if="plan.popular" small text-color="white" class="bg-indigo ml-2">Most Popular</v-chip>
              </v-card-title>
              <v-card-text>
                <div class="d-flex align-baseline">
                  <span class="text-h3 font-weight-bold">${{ plan.price }}</span>
                  <span class="ml-1 grey--text">/month</span>
                </div>
                <div class="grey--text mb-4">{{ plan.description }}</div>
                <v-divider class="mb-4"></v-divider>
                <div v-for="(feature, j) in plan.features" :key="j" class="d-flex align-center mb-2">
                  <v-icon color="green" small class="mr-2" :icon="mdiCheckCircle"></v-icon>
                  <span>{{ feature }}</span>
                </div>
              </v-card-text>
              <v-spacer></v-spacer>
              <v-card-actions class="pa-4">
                <v-btn block :class="plan.popular ? 'bg-indigo text-white' : 'bg-grey-lighten-3'"> Get Started </v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </section>
    <!-- Footer -->
    <v-footer class="bg-grey-darken-4" padless>
      <v-container>
        <v-row class="py-6">
          <v-col cols="12" md="3">
            <div class="d-flex align-center mb-4">
              <v-avatar color="" class="bg-indigo-lighten-1 mr-3">
                <v-icon :icon="mdiRobot" color="white"></v-icon>
              </v-avatar>
              <span class="text-h6 font-weight-bold">Toelbot AI</span>
            </div>
            <p class="grey--text">Intelligent AI chatbots for modern customer support.</p>
          </v-col>

          <v-col cols="12" md="3">
            <h4 class="mb-4">Product</h4>
            <div class="d-flex flex-column">
              <a href="#features" class="text-grey-lighten-3 mb-2 text-decoration-none">Features</a>
              <a href="#pricing" class="text-grey-lighten-3 mb-2 text-decoration-none">Pricing</a>
              <a href="#" class="text-grey-lighten-3 mb-2 text-decoration-none">Integrations</a>
            </div>
          </v-col>

          <v-col cols="12" md="3">
            <h4 class="mb-4">Resources</h4>
            <div class="d-flex flex-column">
              <a href="#" class="text-grey-lighten-3 mb-2 text-decoration-none">Documentation</a>
              <a href="#" class="text-grey-lighten-3 mb-2 text-decoration-none">Blog</a>
              <a href="#" class="text-grey-lighten-3 mb-2 text-decoration-none">Support</a>
            </div>
          </v-col>

          <v-col cols="12" md="3">
            <h4 class="mb-4">Company</h4>
            <div class="d-flex flex-column">
              <a href="#" class="text-grey-lighten-3 mb-2 text-decoration-none">About</a>
              <a href="#" class="text-grey-lighten-3 mb-2 text-decoration-none">Careers</a>
              <a href="#" class="text-grey-lighten-3 mb-2 text-decoration-none">Contact</a>
            </div>
          </v-col>
        </v-row>

        <v-divider class="grey darken-1"></v-divider>

        <div class="d-flex flex-column flex-md-row justify-space-between align-center py-4">
          <div class="grey--text mb-4 mb-md-0">© 2025 Toelbot. All rights reserved.</div>
          <div>
            <v-btn icon color="grey-lighten-1">
              <v-icon :icon="mdiTwitter" color="indigo"></v-icon>
            </v-btn>
            <v-btn icon color="grey-lighten-1" class="mx-1">
              <v-icon :icon="mdiLinkedin" color="indigo"></v-icon>
            </v-btn>
            <v-btn icon color="grey-lighten-1" class="mx-1">
              <v-icon :icon="mdiFacebook" color="indigo"></v-icon>
            </v-btn>
          </div>
        </div>
      </v-container>
    </v-footer>
  </v-main>
</template>
<style lang="css" scoped>
.chat-message {
  animation: fadeIn 0.3s ease-in-out;
}
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.custom-gradient {
  background: linear-gradient(to right, #4f46e5, #7c3aed);
}
.chat-container {
  height: 300px;
  overflow-y: auto;
}

/* Horizontal List Styles */
</style>

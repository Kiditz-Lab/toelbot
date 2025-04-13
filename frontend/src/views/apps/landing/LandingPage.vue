<script setup>
import { ref } from 'vue';
import {
  mdiRobot,
  mdiSend,
  mdiChatProcessing,
  mdiChartLine,
  mdiPackageVariantClosed,
  mdiBrain,
  mdiAccount,
  mdiTwitter,
  mdiLinkedin,
  mdiFacebook,
  mdiCheckCircle
} from '@mdi/js';
// Drawer & UI state
const drawer = ref(false);
const userInput = ref('');
const isTyping = ref(false);

// Messages
const messages = ref([
  { id: 1, type: 'user', text: 'Hello! I have a question about my recent order #12345.' },
  {
    id: 2,
    type: 'bot',
    text: "Hi there! I'd be happy to help with your order #12345. It was shipped yesterday and should arrive by Thursday. Is there anything specific you'd like to know?"
  },
  { id: 3, type: 'user', text: 'Can I change the shipping address?' }
]);

// Stats
const stats = ref([
  { value: '85%', label: 'Reduction in Support Costs' },
  { value: '24/7', label: 'Customer Support' },
  { value: '95%', label: 'Customer Satisfaction' },
  { value: '3 min', label: 'Average Integration Time' }
]);

// Features
const features = ref([
  {
    icon: mdiChatProcessing,
    title: 'Real-time Customer Engagement',
    description: 'Provide instant responses to customer inquiries 24/7, enhancing satisfaction and reducing wait times.'
  },
  {
    icon: mdiChartLine,
    title: 'Order & Payment Status Updates',
    description: 'Allow customers to track orders and verify payment status in real-time without waiting for support staff.'
  },
  {
    icon: mdiPackageVariantClosed,
    title: 'Product Information',
    description: 'Instantly deliver accurate product details, availability, and specifications to interested customers.'
  },
  {
    icon: mdiBrain,
    title: 'AI-Powered Intelligence',
    description: 'Our advanced AI learns from each interaction to provide increasingly personalized responses.'
  }
]);

// Steps
const steps = ref([
  {
    title: 'Connect Your Data',
    description: 'Integrate with your existing CRM, e-commerce platform, and knowledge base in minutes.'
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
    quote: 'Implementing ConvoAI increased our conversion rate by 35% and drastically reduced our customer service workload.',
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

// FAQs
// const faqs = ref([
//   {
//     question: 'How quickly can I get started with ConvoAI?',
//     answer:
//       'Most customers have their chatbot up and running within hours. Our setup wizard will guide you through the process, and our team is available to help if you need assistance.'
//   },
//   {
//     question: 'Do I need technical knowledge to use ConvoAI?',
//     answer:
//       'No technical knowledge is required. Our platform is designed to be user-friendly and intuitive. If you can use a web browser, you can set up and manage your AI chatbot.'
//   },
//   {
//     question: 'Can I customize the chatbot to match my brand?',
//     answer:
//       'Absolutely! You can customize the appearance, tone, and behavior of your chatbot to match your brand guidelines and voice. Upload your logo, choose your colors, and even adjust the conversational style.'
//   },
//   {
//     question: 'What systems can ConvoAI integrate with?',
//     answer:
//       'ConvoAI integrates with popular e-commerce platforms (Shopify, WooCommerce, Magento), CRMs (Salesforce, HubSpot), helpdesks (Zendesk, Freshdesk), and payment processors. We also offer a robust API for custom integrations.'
//   },
//   {
//     question: 'How accurate is the AI in answering customer questions?',
//     answer:
//       'Our AI typically achieves 95%+ accuracy rates for industry-specific queries after initial training. The system continues to learn from interactions, becoming more accurate over time.'
//   }
// ]);

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
      <v-avatar color="indigo" class="mx-3">
        <v-icon color="white" :icon="mdiRobot"></v-icon>
      </v-avatar>
      <v-toolbar-title class="font-weight-bold">Toelbot AI</v-toolbar-title>
    </div>
    <v-spacer></v-spacer>

    <!-- Desktop Navigation Menu -->
    <div class="d-none d-md-flex mr-3">
      <v-btn variant="text" href="#features" class="text-none">Features</v-btn>
      <v-btn variant="text" href="#usecase" class="text-none">UseCase</v-btn>
      <v-btn variant="text" href="#pricing" class="text-none">Pricing</v-btn>
      <v-btn variant="text" href="#faq" class="text-none">FAQ</v-btn>
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
          <v-col cols="12" md="6" class="text-white">
            <h1 class="text-h1 font-weight-bold mb-4">Transform Customer Support with AI-Powered Chat</h1>
            <p class="text-36 mb-6">
              Real-time answers for orders, payments, and product information. Enhance customer experience while reducing support costs.
            </p>
            <div class="d-flex flex-column flex-sm-row">
              <v-btn x-large color="white" class="text-none text-indigo font-weight-bold mb-4 mb-sm-0 mr-sm-4">Get Started</v-btn>
              <v-btn x-large variant="outlined" color="white" class="text-none font-weight-bold">Watch Demo</v-btn>
            </div>
          </v-col>
          <v-col cols="12" md="6">
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
    <!-- Features Section -->
    <section id="features" class="grey lighten-4 py-12">
      <v-container>
        <div class="text-center mb-12">
          <h2 class="text-h2 font-weight-bold mb-4">Power Up Your Customer Support</h2>
          <p class="text-h3 text-grey">
            Our AI chatbot integrates seamlessly with your existing systems to provide real-time information and support.
          </p>
        </div>

        <v-row>
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
        </v-row>
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
    <section class="grey lighten-4 py-12">
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
          <div class="grey--text mb-4 mb-md-0">© 2025 ConvoAI. All rights reserved.</div>
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
</style>

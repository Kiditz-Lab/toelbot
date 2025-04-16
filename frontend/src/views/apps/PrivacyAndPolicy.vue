<script setup lang="ts">
import { ref } from 'vue';
import { MdPreview } from 'md-editor-v3';
import('md-editor-v3/lib/style.css');
const policy = ref(`
# Privacy Policy for Toelbox

**Effective Date:** 16-Apr-2025

Thank you for using **Toelbox** ("Service"). We are committed to protecting your privacy. This Privacy Policy explains how we collect, use, and protect your information when you use our chatbot, which is integrated with platforms such as Facebook, Instagram, Slack, and Discord.

---

## 1. Information We Collect

When you interact with Toelbox, we may collect the following information:

### Personal Identifiable Information (PII):
- Full Name  
- Email Address  
- Location (as provided or inferred from platform)

### Chat and Interaction Data:
- Messages exchanged with the chatbot  
- Usage behavior and preferences

### Platform Information:
- User ID and profile data from Facebook, Instagram, Slack, or Discord

### Technical Information:
- IP address, browser type, and device data

---

## 2. How We Use Your Information

We use your data to:

- Provide and improve the Toelbox chatbot experience  
- Personalize your user interactions  
- Communicate with you about updates, support, or features  
- Ensure compliance with legal and regulatory requirements  
- Analyze usage patterns for improvement and development

---

## 3. Data Sharing and Third-Party Services

We may share your information with trusted third-party services to help operate and improve Toelbox, including:

- Facebook and Instagram (Meta Platforms, Inc.)  
- Slack Technologies, LLC  
- Discord Inc.  
- Other service providers for analytics, cloud hosting, and infrastructure

> We **do not sell** your personal information to third parties.

---

## 4. Data Storage and Retention

Your chat history and user data are stored securely. We retain data for as long as it is necessary to provide our service, comply with legal obligations, or resolve disputes.

---

## 5. Your Rights

As a user in Indonesia, you have the right to:

- Access and review your personal data  
- Request corrections or deletions  
- Withdraw consent at any time  
- Contact us regarding your privacy

To make a request, please email us at **support@toelbox.com**.

---

## 6. Security

We take appropriate technical and organizational measures to protect your data. However, please be aware that no internet-based service is 100% secure.

---

## 7. Children’s Privacy

Toelbox is not intended for users under 13 years of age. We do not knowingly collect data from children.

---

## 8. Changes to This Policy

We may update this Privacy Policy from time to time. Updates will be posted with a new effective date.

---

## 9. Contact Us

If you have any questions about this Privacy Policy or your data, please contact us:

📧 Email: **support@toelbox.com**

`);
</script>

<!-- components/PrivacyPolicy.vue -->
<template>
  <v-container class="py-8">
    <v-row justify="center">
      <v-col cols="12" md="10" lg="8">
        <v-card elevation="4" class="pa-6 rounded-xl">
          <v-card-title class="text-h5 font-weight-bold mb-4"> Privacy Policy for Toelbox </v-card-title>
          <v-divider class="mb-4" />
          <!-- <div v-html="compiledMarkdown" class="markdown-body" /> -->
          <MdPreview :model-value="policy" preview-theme="github" />
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

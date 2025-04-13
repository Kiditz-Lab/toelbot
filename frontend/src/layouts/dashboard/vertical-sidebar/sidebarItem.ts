// icons
// import {
//   QuestionOutlined,
//   DashboardOutlined,
//   ChromeOutlined,
//   LoginOutlined,
//   ProfileOutlined,
//   FontSizeOutlined,
//   BgColorsOutlined,
//   BarcodeOutlined,
//   CrownOutlined
// } from '@ant-design/icons-vue';

// import { SettingOutlined, MessageOutlined, FileTextOutlined, CloudUploadOutlined, GlobalOutlined } from '@ant-design/icons-vue';
import { CloudIcon, MessageCircle2Icon, MessageCircleSearchIcon, FileTextIcon, WorldIcon, SettingsIcon, ToolsIcon } from 'vue-tabler-icons';
// import { ChartBarIcon } from 'vue-tabler-icons';

export interface menu {
  header?: string;
  title?: string;
  icon?: object;
  to?: string;
  divider?: boolean;
  chip?: string;
  chipColor?: string;
  chipVariant?: string;
  chipIcon?: string;
  children?: menu[];
  disabled?: boolean;
  type?: string;
  subCaption?: string;
}

const sidebarItem: menu[] = [
  // { header: 'Navigation' },
  {
    title: 'Overview',
    icon: MessageCircle2Icon,
    to: 'Chats'
  },
  { header: 'Activity' },
  {
    title: 'Chat History',
    icon: MessageCircleSearchIcon,
    to: 'ChatHistory'
  },
  { header: 'AI Knowledge' },
  {
    title: 'Files',
    icon: CloudIcon,
    to: 'TrainingFile'
  },
  {
    title: 'Text',
    icon: FileTextIcon,
    to: 'TrainingText'
  },
  {
    title: 'Website',
    icon: WorldIcon,
    to: 'TrainingWebsite'
  },
  { header: 'Advance' },
  {
    title: 'Tools',
    icon: ToolsIcon,
    to: 'Tools'
  },
  {
    title: 'Settings',
    icon: SettingsIcon,
    to: 'Settings'
  },
  
  // {
  //   title: 'Color',
  //   icon: BgColorsOutlined,
  //   to: '/colors'
  // },
  // {
  //   title: 'Shadow',
  //   icon: BarcodeOutlined,
  //   to: '/shadow'
  // },
  // {
  //   title: 'Ant Icons',
  //   icon: CrownOutlined,
  //   to: '/icon/ant'
  // },
  // { header: 'Support' },
  // {
  //   title: 'Sample Page',
  //   icon: ChromeOutlined,
  //   to: '/sample-page'
  // },
  // {
  //   title: 'Documentation',
  //   icon: QuestionOutlined,
  //   to: 'https://codedthemes.gitbook.io/mantis-vuetify/',
  //   type: 'external',
  //   chip: 'gitbook',
  //   chipColor: 'secondary',
  //   chipVariant: 'flat'
  // }
];

// const sidebarItem: menu[] = [
//   { header: 'Navigation' },
//   {
//     icon: FontSizeOutlined,
//     title: 'Text Tools',
//     children: [
//       {
//         title: 'Text Diff',
//         to: '/text-diff'
//       },
//       {
//         title: 'Loren Ipsum',
//         to: '/loren-ipsum'
//       },
//       {
//         title: 'Letter Counter',
//         to: '/letter-counter'
//       },
//       {
//         title: 'Text Splitter',
//         to: '/split-text'
//       },
//       {
//         title: 'Text Replacer',
//         to: '/replace-text'
//       },

//     ]
//   },
//   {
//     icon: FileImageOutlined,
//     title: 'Image Tools',
//     children: [
//       {
//         title: 'Image to Base64',
//         to: '/base64-image'
//       }
//     ]
//   },
//   {
//     icon: DatabaseOutlined,
//     title: 'Data Tools',
//     children: [
//       {
//         title: 'CSV 2 Json',
//         to: '/csv-2-json'
//       },
//       {
//         title: 'Json 2 CSV',
//         to: '/json-2-csv'
//       }
//     ]
//   }
// ];

export default sidebarItem;

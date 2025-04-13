import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';

const firebaseConfig = {
  apiKey: 'AIzaSyDvqvusCE__n3KB8lsgzGSa_5cl9ql2OVA',
  authDomain: 'toelbox-db989.firebaseapp.com',
  projectId: 'toelbox-db989',
  storageBucket: 'toelbox-db989.appspot.com',
  messagingSenderId: '853330087183',
  appId: '1:853330087183:web:69a4802c0106911396c27a',
  measurementId: 'G-1WP99QBJBK'
};
const firebaseApp = initializeApp(firebaseConfig);
const auth = getAuth(firebaseApp);
export { auth };

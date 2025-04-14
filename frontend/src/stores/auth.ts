import { defineStore } from 'pinia';
import { router } from '@/router';
// import { fetchWrapper } from '@/utils/helpers/fetch-wrapper';

import { GoogleAuthProvider, signInWithPopup, signOut, signInWithEmailAndPassword } from 'firebase/auth';
import { auth } from '@/utils/firebase';

// const baseUrl = `${import.meta.env.VITE_API_URL}/users`;

export const useAuthStore = defineStore('auth', {
  state: () => ({
    // initialize state from local storage to enable user to stay logged in
    /* eslint-disable-next-line @typescript-eslint/ban-ts-comment */
    // @ts-ignore
    user: JSON.parse(localStorage.getItem('user')),
    token: localStorage.getItem('token'),
    returnUrl: null,
    loading: false,
    error: ''
  }),
  actions: {
    async login(username: string, password: string) {
      this.loading = true;
      this.error = '';

      try {
        const userCredential = await signInWithEmailAndPassword(auth, username, password);
        this.user = userCredential.user;
        this.token = await userCredential.user.getIdToken();

        // Store user details in local storage
        localStorage.setItem('user', JSON.stringify(this.user));
        localStorage.setItem('token', this.token);

        // Redirect to returnUrl or dashboard
        router.push(this.returnUrl || '/agents');
      } catch (error: unknown) {
        console.error('Login Error:', error);
        this.error = (error as Error).message;
      } finally {
        this.loading = false;
      }
    },

    async loginWithGoogle() {
      const provider = new GoogleAuthProvider();
      this.loading = true;

      try {
        try {
          const result = await signInWithPopup(auth, provider);
          this.user = result.user;
          this.token = await result.user.getIdToken();

          localStorage.setItem('user', JSON.stringify(this.user));
          localStorage.setItem('token', this.token);
          router.push(this.returnUrl || '/agents');
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        } catch (error: Error | any) {
          console.error('Google Login Error:', error);
          this.error = error.message;
        } finally {
          this.loading = false;
        }

        router.push(this.returnUrl || '/agents');
      } catch (error: unknown) {
        console.error('Login Error:', error);
        this.error = (error as Error).message;
      } finally {
        this.loading = false;
      }
    },

    async logout() {
      await signOut(auth);
      this.user = null;
      this.token = null;
      localStorage.clear();
      sessionStorage.clear();
      router.push('/login');
    }
  }
});

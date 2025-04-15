import { ref } from 'vue';
import axios, { type AxiosInstance, type AxiosProgressEvent, type AxiosError } from 'axios';
import { auth } from '@/utils/firebase';
import { onAuthStateChanged } from 'firebase/auth';

export function useApi() {
  const baseURL = `${import.meta.env.VITE_API_URL}`;
  const loading = ref<boolean>(false);
  const error = ref<string | null>(null);
  const uploadProgress = ref<number>(0);
  const downloadProgress = ref<number>(0);
  const api: AxiosInstance = axios.create({ baseURL });

  async function getAuthToken(): Promise<string> {
    return new Promise((resolve, reject) => {
      const user = auth.currentUser;
      if (user) {
        user.getIdToken().then(resolve).catch(reject);
      } else {
        const unsubscribe = onAuthStateChanged(auth, async (user) => {
          unsubscribe();
          if (user) {
            resolve(await user.getIdToken());
          } else {
            reject(new Error('User is not authenticated'));
          }
        });
      }
    });
  }

  async function request<T>(
    method: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE',
    url: string,
    data: unknown = null,
    params: Record<string, unknown> = {},
    headers: Record<string, unknown> = {},
    responseType: 'json' | 'stream' = 'json'
  ): Promise<T> {
    loading.value = true;
    error.value = null;
    uploadProgress.value = 0;
    downloadProgress.value = 0;

    try {
      const token = await getAuthToken();
      const response = await api.request<T>({
        method,
        url,
        data,
        params,
        paramsSerializer: (params) => {
          const searchParams = new URLSearchParams();
          for (const [key, value] of Object.entries(params)) {
            if (Array.isArray(value)) {
              value.forEach((item) => searchParams.append(key, item));
            } else {
              searchParams.append(key, String(value));
            }
          }
          return searchParams.toString();
        },
        headers: { Authorization: `Bearer ${token}`, ...headers },
        responseType,
        onUploadProgress: (progressEvent: AxiosProgressEvent) => {
          if (progressEvent.total) {
            uploadProgress.value = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          }
        },
        onDownloadProgress: (progressEvent: AxiosProgressEvent) => {
          if (progressEvent.total) {
            downloadProgress.value = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          }
        }
      });

      return response.data;
    } catch (err: unknown) {
      const axiosErr = err as AxiosError;

      if (axiosErr.code === 'ECONNABORTED') {
        error.value = 'Request timed out. Please try again.';
      } else if (!axiosErr.response) {
        error.value = 'Network error. Please check your internet connection.';
      } else {
        const status = axiosErr.response?.status;
        const message = axiosErr.response?.data || axiosErr.message;
        error.value = `Error ${status}: ${typeof message === 'string' ? message : JSON.stringify(message)}`;
      }

      return Promise.reject(error.value);
    } finally {
      loading.value = false;
    }
  }

  return {
    loading,
    error,
    uploadProgress,
    downloadProgress,
    getAuthToken,
    get: <T>(url: string, params?: Record<string, unknown>, headers?: Record<string, unknown>, responseType: 'json' | 'stream' = 'json') =>
      request<T>('GET', url, null, params, headers, responseType),
    post: <T>(url: string, data?: unknown, headers?: Record<string, unknown>, responseType: 'json' | 'stream' = 'json') =>
      request<T>('POST', url, data, {}, headers, responseType),
    put: <T>(url: string, data?: unknown, headers?: Record<string, unknown>, responseType: 'json' | 'stream' = 'json') =>
      request<T>('PUT', url, data, {}, headers, responseType),
    patch: <T>(url: string, data?: unknown, headers?: Record<string, unknown>, responseType: 'json' | 'stream' = 'json') =>
      request<T>('PATCH', url, data, {}, headers, responseType),
    del: <T>(url: string, params?: Record<string, unknown>, headers?: Record<string, unknown>, responseType: 'json' | 'stream' = 'json') =>
      request<T>('DELETE', url, null, params, headers, responseType),
  };
}

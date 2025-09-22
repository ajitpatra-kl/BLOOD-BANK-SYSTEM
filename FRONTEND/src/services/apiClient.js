import axios from 'axios';

// Base API configuration 
// In Docker: Nginx will proxy /api to backend service
// In development: Use localhost:8080
const API_BASE_URL = import.meta.env.PROD ? '/api' : 'http://localhost:8080/api';

// Create axios instance with default configuration
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 seconds timeout
});

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    // You can add auth headers here if needed
    console.log('Making request to:', config.url);
    console.log('Request data:', config.data);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
apiClient.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    console.error('API Error:', error.response?.data?.message || error.message);
    return Promise.reject(error.response?.data || error);
  }
);

export default apiClient;
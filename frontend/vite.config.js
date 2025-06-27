import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/tx': 'http://localhost:8080',
      '/cron': 'http://localhost:8080',
      '/roundup': 'http://localhost:8080',
      '/config': 'http://localhost:8080',
      '/api': 'http://localhost:8080'
    }
  }
})
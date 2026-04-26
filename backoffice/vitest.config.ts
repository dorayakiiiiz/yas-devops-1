import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    include: [
      'test/**/*.{test,spec}.{js,jsx,ts,tsx}',        // THÊM DÒNG NÀY
      'common/**/*.{test,spec}.{js,jsx,ts,tsx}',
      'modules/**/*.{test,spec}.{js,jsx,ts,tsx}',
      'pages/**/*.{test,spec}.{js,jsx,ts,tsx}',
      'utils/**/*.{test,spec}.{js,jsx,ts,tsx}'
    ],
    exclude: [
      'node_modules/**',
      '.next/**',
      'public/**',
      'styles/**',
      'constants/**'
    ],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov'],
      reportsDirectory: './coverage',
      include: [
        'common/**/*.{js,jsx,ts,tsx}',
        'modules/**/*.{js,jsx,ts,tsx}',
        'pages/**/*.{js,jsx,ts,tsx}',
        'utils/**/*.{js,jsx,ts,tsx}'
      ],
      exclude: [
        'test/**',                   
        '**/*.{test,spec}.{js,jsx,ts,tsx}',
        '**/node_modules/**',
        '**/.next/**',
        '**/constants/**',
        '**/public/**',
        '**/styles/**'
      ]
    },
    globals: true
  }
})
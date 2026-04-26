import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    include: [
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
      'constants/**'  // Bỏ qua constants nếu không muốn test
    ],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov'],
      reportsDirectory: './coverage',
      include: [
        '_test_/**/*.{test,spec}.{js,jsx,ts,tsx}',
        'common/**/*.{js,jsx,ts,tsx}',
        'modules/**/*.{js,jsx,ts,tsx}',
        'pages/**/*.{js,jsx,ts,tsx}',
        'utils/**/*.{js,jsx,ts,tsx}'
      ],
      exclude: [
        '**/*.{test,spec}.{js,jsx,ts,tsx}',
        '**/node_modules/**',
        '**/.next/**',
        '**/constants/**',  
        '**/public/**',
        '**/styles/**'
      ],
      thresholds: {
        lines: 70,
        functions: 70,
        branches: 65,
        statements: 70
      }
    },
    globals: true
  }
})
import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'json-summary', 'html'],
      include: ['**/*.{ts,tsx}'],  // ✅ Quét tất cả file TS/TSX
      exclude: [
        '**/node_modules/**',
        '**/.next/**',
        '**/coverage/**',
        '**/*.d.ts',
        '**/*.test.{ts,tsx}',
        '**/vitest.config.ts',
        '**/next.config.js',
        '**/next-env.d.ts',
      ],
    },
  },
})
import { defineConfig } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';
import tsconfigPaths from 'vite-tsconfig-paths';

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [svelte(), tsconfigPaths()],
    resolve: {
        alias: {
            $style: '/src/lib/style',
        },
    },
});

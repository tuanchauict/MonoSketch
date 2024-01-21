import { defineConfig } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';
import sassPlugin from 'vite-plugin-sass';
import tsconfigPaths from 'vite-tsconfig-paths';

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [svelte(), sassPlugin(), tsconfigPaths()],
    resolve: {
        alias: {
            $style: '/src/lib/style',
        },
    },
});

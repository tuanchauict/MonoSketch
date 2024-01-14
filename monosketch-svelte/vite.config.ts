import { defineConfig } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';
import sassPlugin from 'vite-plugin-sass';

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [svelte(), sassPlugin()],
    resolve: {
        alias: {
            $app: '/src/app',
            $mono: '/src/lib/mono',
            $style: '/src/lib/style',
            $assets: '/src/assets',
            $ui: '/src/lib/ui',
            $libs: '/src/lib',
        },
    },
});

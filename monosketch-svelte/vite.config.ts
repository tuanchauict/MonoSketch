import { defineConfig } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';
import tsconfigPaths from 'vite-tsconfig-paths';

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [svelte(), tsconfigPaths()],
    build: {
        minify: 'terser', // 'esbuild' (default), 'terser', or false to disable
        terserOptions: {
            // Only used when minify: 'terser'
            compress: {
                drop_console: true,
                drop_debugger: true,
                dead_code: true,
                pure_funcs: [],
                booleans: true,
                collapse_vars: true,
                conditionals: true,
                comparisons: true,
            }
        },
        sourcemap: true,
        rollupOptions: {
            output: {
                assetFileNames: (assetInfo) => {
                    // Keep original filenames for fonts
                    if (/\.(woff|woff2|ttf|otf|eot)$/.test(assetInfo.name)) {
                        return 'fonts/[name][extname]';
                    }
                    // Use content hash for other assets
                    return 'assets/[name]-[hash][extname]';
                }
            }
        }
    },
    resolve: {
        alias: {
            $style: '/src/lib/style',
        },
    },
});

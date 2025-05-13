module.exports = {
    parser: '@typescript-eslint/parser',
    env: {
        "browser": true,
        "es2021": true
    },
    extends: [
        "plugin:svelte/recommended",
        "plugin:@typescript-eslint/recommended"
    ],
    overrides: [
        {
            env: {
                "node": true
            },
            files: [
                ".eslintrc.{js,cjs}"
            ],
            parserOptions: {
                sourceType: "script"
            }
        },
        {
            files: ["src/**/*.svelte", "src/*.svelte"],
            parser: 'svelte-eslint-parser',
            parserOptions: {
                parser: '@typescript-eslint/parser',
            }
        },
    ],
    parserOptions: {
        ecmaVersion: "latest",
        sourceType: "module",
        project: './tsconfig.json',
        extraFileExtensions: ['.svelte']
    },
    ignorePatterns: [
        "vite.config.ts",
        "svelte.config.js",
    ],
    rules: {
        "indent": ["error", 4, {
            "SwitchCase": 1,
        }],
        "semi": ["error", "always"],
        "@typescript-eslint/indent": ["error", 4],
        "space-before-function-paren": ["error", {
            "anonymous": "never",
            "named": "never",
            "asyncArrow": "always",
        }],
        "svelte/valid-compile": "off", // temporary ignore this rule until fixing a11y issues
        "@typescript-eslint/no-namespace": "off", // temporary ignore this rule
        "svelte/no-inner-declarations": "off",
        "object-curly-spacing": ["error", "always"],
        "@typescript-eslint/no-unused-vars": ["warn", {
            "vars": "all",
            "args": "after-used",
            "ignoreRestSiblings": true,
            "varsIgnorePattern": "^_",
            "argsIgnorePattern": "^_",
            "caughtErrors": "all",
            "caughtErrorsIgnorePattern": "^_"
        }],
        "eol-last": ["error", "always"],
        // always enforce === and !==
        "eqeqeq": "error",
    }
}

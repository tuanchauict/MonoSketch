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
            files: ['src/**/*.svelte'],
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
    rules: {
        "@typescript-eslint/indent": ["error", 4],
        "space-before-function-paren": ["error", {
            "anonymous": "never",
            "named": "never",
            "asyncArrow": "always",
        }],
        "svelte/valid-compile": "off", // temporary ignore this rule until fixing a11y issues
        "indent": "off", // use @typescript-eslint/indent instead
    }
}

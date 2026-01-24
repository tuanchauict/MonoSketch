/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/main/resources/**/*.html",
    "./src/main/kotlin/**/*.kt",
    "./libs/**/src/main/kotlin/**/*.kt",
    "./libs/**/src/jsMain/kotlin/**/*.kt",
    "./app/src/main/kotlin/**/*.kt",
  ],
  // Use 'dark' class on html element (already present in your index.html)
  darkMode: 'class',
  theme: {
    extend: {
      fontFamily: {
        mono: ["'Jetbrains Mono'", 'Menlo', "'Courier New'", 'Courier', 'monospace'],
        ui: ["'Roboto'", 'Arial', 'serif'],
      },
      spacing: {
        'nav': '48px',
        'shape-tools': '250px',
        'canvas-left': '33px',
        'canvas-top': '18px',
      },
      zIndex: {
        'keyboard-shortcuts': '200',
        'export-text': '201',
        'edit-text': '202',
        'dropdown-menu': '203',
        'rename-project': '205',
        'all-projects': '500',
        'remove-project': '501',
        'tooltip': '10000',
      },
      colors: {
        // You can add custom colors here that map to your CSS variables
        // Example: 'workspace-bg': 'var(--workspace-bg-color)',
      },
    },
  },
  plugins: [],
  // Prefix Tailwind classes to avoid conflicts with existing SCSS
  // Remove this if you want standard Tailwind classes
  // prefix: 'tw-',
}

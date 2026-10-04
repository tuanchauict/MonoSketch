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
        mono: 'var(--monospace-font)',
        ui: ["'Roboto'", 'Arial', 'serif'],
      },
      spacing: {
        'nav': 'var(--nav-height)',
        'shape-tools': '250px',
        'canvas-left': 'var(--canvas-left)',
        'canvas-top': 'var(--canvas-top)',
      },
      zIndex: {
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

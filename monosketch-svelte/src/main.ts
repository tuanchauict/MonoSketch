import "$assets/fonts/stylesheet.css"
import "$style/main.scss"
import App from './App.svelte'

const app = new App({
    // @ts-ignore
    target: document.getElementById('app'),
})

export default app

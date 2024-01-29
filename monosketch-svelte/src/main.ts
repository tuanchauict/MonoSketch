import '$assets/fonts/stylesheet.css';
import '$style/main.scss';
import App from './App.svelte';
import { AppContext } from '$app/app-context';
import { APP_CONTEXT } from '$mono/common/constant';
import { WindowViewModel } from '$mono/window/window-viewmodel';

const appContext = new AppContext();
const context = new Map();
context.set(APP_CONTEXT, appContext);

window.onresize = () => {
    WindowViewModel.windowSizeUpdateEventFlow.value = true;
}

const app = new App({
    // @ts-ignore
    target: document.getElementById('app'),
    context: context,
});

appContext.onStart();
export default app;

import '$assets/fonts/stylesheet.css';
import '$style/main.scss';
import { AppContext } from '$app/app-context';
import { APP_CONTEXT } from '$mono/common/constant';
import { WindowViewModel } from '$mono/window/window-viewmodel';
import App from './App.svelte';

const appContext = new AppContext();
const context = new Map();
context.set(APP_CONTEXT, appContext);

window.onresize = () => {
    WindowViewModel.windowSizeUpdateEventFlow.value = true;
};

window.onfocus = () => {
    WindowViewModel.applicationActiveStateFlow.value = true;
};

window.onblur = () => {
    WindowViewModel.applicationActiveStateFlow.value = false;
};

document.addEventListener('visibilitychange', () => {
    WindowViewModel.applicationActiveStateFlow.value = document['visibilityState'] === 'visible';
});

const app = new App({
    // @ts-expect-error - Safe to ignore
    target: document.getElementById('app'),
    context: context,
});

appContext.onStart();
export default app;

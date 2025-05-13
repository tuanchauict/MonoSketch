import type { AppContext } from "$app/app-context";
import { getContext } from "svelte";

export const APP_CONTEXT = 'app-context';

export function getAppContext(): AppContext {
    return getContext<AppContext>(APP_CONTEXT);
}

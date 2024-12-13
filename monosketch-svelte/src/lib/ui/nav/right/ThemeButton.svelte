<script lang="ts">
import { getContext, onDestroy, onMount } from 'svelte';
import { APP_CONTEXT } from '$mono/common/constant';
import { AppContext } from '$app/app-context';
import { LifecycleOwner } from 'lib/libs/flow';
import ThemeModeIcon from '$ui/nav/right/ThemeModeIcon.svelte';
import { ThemeMode } from '$mono/ui-state-manager/states';
import {UiStatePayload} from "$mono/ui-state-manager/ui-state-payload";

let themeMode: ThemeMode;

const appContext = getContext<AppContext>(APP_CONTEXT);
const lifecycleOwner = new LifecycleOwner();

onMount(() => {
    lifecycleOwner.onStart();

    appContext.appUiStateManager.themeModeFlow.observe(lifecycleOwner, (mode) => {
        themeMode = mode;
    });
});

onDestroy(() => {
    lifecycleOwner.onStop();
});

function updateTheme(mode: ThemeMode) {
    appContext.appUiStateManager.updateUiState(UiStatePayload.ChangeTheme(mode));
}
</script>

{#if themeMode === ThemeMode.DARK}
    <ThemeModeIcon onChangeTheme="{updateTheme}" themeMode="{ThemeMode.DARK}" />
{:else}
    <ThemeModeIcon onChangeTheme="{updateTheme}" themeMode="{ThemeMode.LIGHT}" />
{/if}

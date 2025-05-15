<script lang="ts">
    import { onMount } from 'svelte';
    import { getAppContext } from '$mono/common/constant';
    import { LifecycleOwner } from 'lib/libs/flow';
    import ThemeModeIcon from '$ui/nav/right/ThemeModeIcon.svelte';
    import { ThemeMode } from '$mono/ui-state-manager/states';
    import { UiStatePayload } from "$mono/ui-state-manager/ui-state-payload";

    let themeMode: ThemeMode;

const appContext = getAppContext();

onMount(() => {
    const lifecycleOwner = LifecycleOwner.start();

    appContext.appUiStateManager.themeModeFlow.observe(lifecycleOwner, (mode) => {
        themeMode = mode;
    });

    return () => {
        lifecycleOwner.onStop();
    };
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

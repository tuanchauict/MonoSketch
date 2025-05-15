<script lang="ts">
    import TooltipTarget from '$ui/modal/tooltip/TooltipTarget.svelte';
    import { TooltipDirection } from '$ui/modal/tooltip/model';
    import { ScrollMode } from '$mono/ui-state-manager/states';
    import AppIcon from '$ui/nav/common/AppIcon.svelte';
    import { scrollModeToContentMap } from '$ui/nav/right/model';
    import { onMount } from 'svelte';
    import { LifecycleOwner } from 'lib/libs/flow';
    import { getAppContext } from '$mono/common/constant';
    import { UiStatePayload } from "$mono/ui-state-manager/ui-state-payload";

    let scrollMode: ScrollMode = ScrollMode.BOTH;
    const appContext = getAppContext();

    onMount(() => {
        const lifecycleOwner = LifecycleOwner.start();

        appContext.appUiStateManager.scrollModeFlow.observe(lifecycleOwner, (value) => {
            scrollMode = value;
        });

        return () => {
            lifecycleOwner.onStop();
        };
    });

    function changeMode() {
        const nextMode = scrollModeToContentMap[scrollMode].next;
        appContext.appUiStateManager.updateUiState(UiStatePayload.ChangeScrollMode(nextMode));
    }
</script>

<TooltipTarget direction="{TooltipDirection.BOTTOM}" text="Scroll mode" offsetVertical="{6.5}">
    <AppIcon size="{16}" viewBoxSize="{40}" onClick="{changeMode}">
        <path d="{scrollModeToContentMap[scrollMode].iconPath}"></path>
    </AppIcon>
</TooltipTarget>

<script lang="ts">
import TooltipTarget from '$ui/modal/tooltip/TooltipTarget.svelte';
import { Direction } from '$ui/modal/tooltip/model';
import { ScrollMode } from '$mono/ui-state-manager/states';
import AppIcon from '$ui/nav/common/AppIcon.svelte';
import { scrollModeToContentMap } from '$ui/nav/right/model';
import { getContext, onDestroy, onMount } from 'svelte';
import { LifecycleOwner } from 'lib/libs/flow';
import { APP_CONTEXT } from '$mono/common/constant';
import type { AppContext } from '$app/app-context';

let scrollMode: ScrollMode = ScrollMode.BOTH;
const lifecycleOwner = new LifecycleOwner();
const appContext = getContext<AppContext>(APP_CONTEXT);

onMount(() => {
    lifecycleOwner.onStart();

    appContext.appUiStateManager.scrollModeFlow.observe(lifecycleOwner, (value) => {
        scrollMode = value;
    });
});

onDestroy(() => {
    lifecycleOwner.onStop();
});

function changeMode() {
    appContext.appUiStateManager.setScrollMode(scrollModeToContentMap[scrollMode].next);
}
</script>

<TooltipTarget direction="{Direction.BOTTOM}" text="Scroll mode" offsetVertical="{6.5}">
    <AppIcon size="{16}" viewBoxSize="{40}" onClick="{changeMode}">
        <path d="{scrollModeToContentMap[scrollMode].iconPath}"></path>
    </AppIcon>
</TooltipTarget>

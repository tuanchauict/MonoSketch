<script lang="ts">
import { themeModeToContentMap } from '$ui/nav/right/model';
import TooltipTarget from '$ui/modal/tooltip/TooltipTarget.svelte';
import AppIcon from '$ui/nav/common/AppIcon.svelte';
import { Direction } from '$ui/modal/tooltip/model';
import { ThemeMode } from '$mono/ui-state-manager/states';

export let themeMode: ThemeMode;
export let onChangeTheme: (themeMode: ThemeMode) => void;

function changeTheme() {
    onChangeTheme(themeModeToContentMap[themeMode].next);
}
</script>

<TooltipTarget
    direction="{Direction.BOTTOM}"
    text="{themeModeToContentMap[themeMode].title}"
    offsetVertical="{6.5}"
>
    <AppIcon onClick="{changeTheme}" size="{20}" viewBoxSize="{16}">
        <path d="{themeModeToContentMap[themeMode].iconPath}"></path>
    </AppIcon>
</TooltipTarget>

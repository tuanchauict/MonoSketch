<script lang="ts">
import { ThemeMode } from '$mono/theme';
import { Direction } from '$ui/modal/tooltip/model';
import { themeModeToContentMap } from '$ui/nav/right/model';
import TooltipTarget from '$ui/modal/tooltip/TooltipTarget.svelte';
import AppIcon from '$ui/nav/common/AppIcon.svelte';

export let themeMode: ThemeMode;
export let onChangeTheme: (themeMode: ThemeMode) => void;

function changeTheme() {
    const nextThemeMode = themeMode === ThemeMode.LIGHT ? ThemeMode.DARK : ThemeMode.LIGHT;
    onChangeTheme(nextThemeMode);
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

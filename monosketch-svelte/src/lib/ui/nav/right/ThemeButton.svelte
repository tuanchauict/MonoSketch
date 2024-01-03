<script lang="ts">
    import AppIcon from "../common/AppIcon.svelte";
    import {ThemeMode, themeModeToContentMap} from "./model";
    import TooltipTarget from "../../modal/menu/tooltip/TooltipTarget.svelte";
    import {Direction} from "../../modal/menu/tooltip/model";

    export let themeMode: ThemeMode = ThemeMode.LIGHT;

    $: updateTheme(themeMode);

    function changeTheme() {
        switch (themeMode) {
            case ThemeMode.DARK:
                themeMode = ThemeMode.LIGHT;
                break;
            case ThemeMode.LIGHT:
                themeMode = ThemeMode.DARK;
                break;
        }
    }

    function updateTheme(mode: ThemeMode) {
        switch (mode) {
            case ThemeMode.LIGHT:
                document.documentElement.classList.remove("dark");
                break;
            case ThemeMode.DARK:
                document.documentElement.classList.add("dark");
                break;
        }
    }
</script>

<TooltipTarget direction={Direction.BOTTOM} text="{themeModeToContentMap[themeMode].title}" offsetVertical={6.5}>
    <AppIcon onClick="{changeTheme}" size={20} viewBoxSize={16}>
        <path d="{themeModeToContentMap[themeMode].iconPath}"/>
    </AppIcon>
</TooltipTarget>

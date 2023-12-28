<script lang="ts">

    import AppIcon from "../common/AppIcon.svelte";
    import {ThemeMode} from "./model";

    export let themeMode: ThemeMode = ThemeMode.LIGHT;

    $: path = getPath(themeMode);
    $: updateTheme(themeMode);

    function getPath(mode: ThemeMode): string {
        switch (mode) {
            case ThemeMode.LIGHT:
                return "M12 8a4 4 0 1 1-8 0 4 4 0 0 1 8 0zM8.5 2.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zm0 11a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zm5-5a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zm-11 0a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zm9.743-4.036a.5.5 0 1 1-.707-.707.5.5 0 0 1 .707.707zm-7.779 7.779a.5.5 0 1 1-.707-.707.5.5 0 0 1 .707.707zm7.072 0a.5.5 0 1 1 .707-.707.5.5 0 0 1-.707.707zM3.757 4.464a.5.5 0 1 1 .707-.707.5.5 0 0 1-.707.707z";
            case ThemeMode.DARK:
                return "M8 11a3 3 0 1 1 0-6 3 3 0 0 1 0 6zm0 1a4 4 0 1 0 0-8 4 4 0 0 0 0 8zm.5-9.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zm0 11a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zm5-5a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zm-11 0a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zm9.743-4.036a.5.5 0 1 1-.707-.707.5.5 0 0 1 .707.707zm-7.779 7.779a.5.5 0 1 1-.707-.707.5.5 0 0 1 .707.707zm7.072 0a.5.5 0 1 1 .707-.707.5.5 0 0 1-.707.707zM3.757 4.464a.5.5 0 1 1 .707-.707.5.5 0 0 1-.707.707z";
        }
    }

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

<div>
<AppIcon onClick="{changeTheme}" size={20} viewBoxSize={16}>
    <path d="{path}" />
</AppIcon>
</div>

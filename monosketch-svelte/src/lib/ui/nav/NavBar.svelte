<script>
    import ToolbarContainer from "./common/ToolbarContainer.svelte";
    import ScrollModeButton from "./right/ScrollModeButton.svelte";
    import {ScrollMode} from "./right/model";
    import AppDropdownMenu from "./right/AppDropdownMenu.svelte";
    import ThemeButton from "./right/ThemeButton.svelte";
</script>

<div>
    <ToolbarContainer>
        <ScrollModeButton scrollMode="{ScrollMode.BOTH}"/>
        <ThemeButton />
        <AppDropdownMenu/>
    </ToolbarContainer>
</div>

<style>
    div {
        display: flex;
        width: 100%;
        height: 100%;

        color: white;
    }
</style>

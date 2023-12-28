<script>
    import ToolbarContainer from "./common/ToolbarContainer.svelte";
    import ScrollModeButton from "./right/ScrollModeButton.svelte";
    import {ScrollMode} from "./right/model";
    import AppDropdownMenu from "./right/AppDropdownMenu.svelte";
    import ThemeButton from "./right/ThemeButton.svelte";
    import Brand from "./brand/Brand.svelte";
</script>

<div>
    <Brand/>

    <ToolbarContainer>
        <ScrollModeButton scrollMode="{ScrollMode.BOTH}"/>
        <ThemeButton />
        <AppDropdownMenu/>
    </ToolbarContainer>
</div>

<style>
    div {
        display: flex;
        justify-content: space-between;
        width: 100%;
        height: 100%;

        color: white;
    }
</style>

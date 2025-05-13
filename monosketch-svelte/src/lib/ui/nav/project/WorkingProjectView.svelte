<script lang="ts">
    import SvgIcon from '../../common/SvgIcon.svelte';
    import { TargetBounds } from '$ui/modal/model';
    import CurrentProjectDropDown from "$ui/modal/menu/current-project/CurrentProjectDropDown.svelte";
    import { onMount } from "svelte";
    import { LifecycleOwner } from "$libs/flow";
    import RenameProjectModal from "$ui/modal/rename-project/RenameProjectModal.svelte";
    import { getProjectDataViewModel } from "$ui/nav/project/constants";

    export let projectId: string;
    export let projectName: string;

    const projectDataViewModel = getProjectDataViewModel();

    let button: HTMLElement;
    let isDropDownMenuVisible = false;
    let isRenameProjectVisible = false;

    $: targetBounds = button ? TargetBounds.fromElement(button) : undefined;

    function showMenu() {
        targetBounds = button ? TargetBounds.fromElement(button) : undefined;
        isDropDownMenuVisible = true;
    }

    function onMenuDismiss() {
        isDropDownMenuVisible = false;
    }

    function updateTargetBounds() {
        targetBounds = button ? TargetBounds.fromElement(button) : undefined;
    }

    onMount(() => {
        const lifecycleOwner = LifecycleOwner.start();
        projectDataViewModel.renamingProjectIdFlow.observe(lifecycleOwner, (id) => {
            isRenameProjectVisible = id !== '';
        });

        return () => {
            lifecycleOwner.onStop();
        };
    });
</script>
<svelte:window on:resize="{updateTargetBounds}"/>

<div class="container">
    <div role="button" tabindex="0" class="info-container"
         bind:this={button}
         on:click="{showMenu}"
         on:keydown="{(e) => e.key === 'Enter' && showMenu()}">
        <span class="info">
            {projectName}
        </span>
        <div class="icon">
            <SvgIcon size="{16}">
                <path
                        d="M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z"
                ></path>
            </SvgIcon>
        </div>
    </div>
</div>

{#if isDropDownMenuVisible && targetBounds}
    <CurrentProjectDropDown {projectId} {targetBounds} onDismiss="{onMenuDismiss}"/>
{/if}

{#if isRenameProjectVisible && targetBounds}
    <RenameProjectModal {projectName} {targetBounds}/>
{/if}

<style lang="scss">
    @import '../../../style/variables.scss';

    $max-text-width: 210px;
    .container {
        display: flex;
        align-items: center;
        height: 100%;
        width: $max-text-width + 31px;
    }

    .info-container {
        display: flex;
        align-items: center;

        border-radius: 6px;
        padding: 8px 4px 8px 8px;
        font-family: $monospaceFont;
        cursor: pointer;

        &:hover {
            background: var(--nav-action-hover-bg);
        }
    }

    .info {
        display: flex;
        align-items: center;

        font-size: 14px;
        max-width: $max-text-width;
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
    }

    .icon {
        display: flex;
        margin-left: 4px;
        padding-top: 4px;
    }
</style>

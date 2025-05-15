<script lang="ts">
import { MouseActionToContentMap, type MouseActionType } from './model';
import TooltipTarget from '../../modal/tooltip/TooltipTarget.svelte';
import SvgIcon from '../../common/SvgIcon.svelte';

export let type: MouseActionType;
export let onSelect: (type: MouseActionType) => void;
export let selected: boolean = false;

function onClick() {
    onSelect(type);
}
</script>

<TooltipTarget text="{MouseActionToContentMap[type].title}" offsetVertical="{8}">
    <button class="action" class:selected on:click="{onClick}">
        <SvgIcon viewBoxSize="{24}" size="{21}">
            <path d="{MouseActionToContentMap[type].iconPath}"></path>
        </SvgIcon>
    </button>
</TooltipTarget>

<style lang="scss">
button {
    background: none;

    display: flex;
    align-items: center;
    justify-content: center;
    width: 42px;
    height: 32px;
    border-radius: 6px;
    border: 1px solid transparent;
    cursor: pointer;
    user-select: none;
    color: inherit;

    &:hover {
        background: var(--nav-action-hover-bg);
    }

    &.selected {
        border-color: var(--nav-action-selected-border);
    }
}
</style>

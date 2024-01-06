<script lang="ts">
import CommonBorderTool from './common/CommonBorderTool.svelte';

let selectedId = 'B1';
let cornerRounded = false;

let dashDash = 1;
let dashGap = 0;
let dashShift = 0;

$: console.log(dashDash, dashGap, dashShift);

function onItemSelect(id: string) {
    selectedId = id;
}

function onCornerRoundedChange(value: boolean) {
    cornerRounded = value;
}
</script>

<CommonBorderTool
    title="Border"
    {onItemSelect}
    {selectedId}
    {cornerRounded}
    onCornerRounded="{onCornerRoundedChange}"
    bind:dashDash
    bind:dashGap
    bind:dashShift
/>

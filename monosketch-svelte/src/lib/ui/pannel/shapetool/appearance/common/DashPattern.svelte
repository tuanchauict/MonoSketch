<script lang="ts">
import NumberTextField from '../../../common/NumberTextField.svelte';

export let dash: number;
export let gap: number;
export let shift: number;
</script>

<div>
    <NumberTextField label="Dash" bind:value="{dash}" minValue="{1}" />
    <NumberTextField label="Gap" bind:value="{gap}" minValue="{0}" />
    <NumberTextField label="Shift" bind:value="{shift}" />
</div>

<style lang="scss">
div {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: center;
    width: 100%;
    margin-top: 8px;
    gap: 6px;
}
</style>

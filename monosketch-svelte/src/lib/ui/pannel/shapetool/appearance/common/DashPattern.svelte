<script lang="ts">
    import NumberTextField from '../../../common/NumberTextField.svelte';
    import type { StrokeDashPattern } from "$ui/pannel/shapetool/viewmodel/models";

    export let dashPattern: StrokeDashPattern | null;
    export let onChange: (dashPattern: StrokeDashPattern) => void;

    let dash: number = dashPattern?.dash ?? 1;
    let gap: number = dashPattern?.gap ?? 0;
    let offset: number = dashPattern?.offset ?? 0;

    function onChangeInternal() {
        onChange({dash, gap, offset})
    }
</script>

<div>
    <NumberTextField label="Dash" bind:value="{dash}" minValue="{1}" onChange={onChangeInternal}/>
    <NumberTextField label="Gap" bind:value="{gap}" minValue="{0}" onChange={onChangeInternal}/>
    <NumberTextField label="Shift" bind:value="{offset}" onChange={onChangeInternal}/>
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

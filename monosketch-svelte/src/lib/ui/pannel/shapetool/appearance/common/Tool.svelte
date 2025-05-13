<script lang="ts">
export let title: string;
export let available: boolean;
</script>

{#if available}
    <div>
        <h4>{title}</h4>
        <slot />
    </div>
{/if}

<style lang="scss">
div {
    margin: 0 0 16px;
}

h4 {
    font-style: normal;
    font-weight: normal;
    margin: 0 0 4px;
    user-select: none;
}
</style>

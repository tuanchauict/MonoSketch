<script lang="ts">
    export let title = "";
    export let hasBorderTop = true;
</script>

<section class:notitle={!title} class:border-top={hasBorderTop}>
    {#if title}
        <h3>{title}</h3>
    {/if}
    <div class="content">
        <slot />
    </div>
</section>

<style lang="scss">
    section {
        display: flex;
        flex-direction: column;
        width: 100%;
        padding: 10px;
        color: var(--shapetool-section-content-color);

        &.notitle {
            padding: 4px 10px;
        }

        &.border-top {
            border-top: 0.75px solid var(--shapetool-section-divider-color);
        }
    }

    h3 {
        margin: 0 0 10px 0;
        padding: 0;
        font-size: 11px;
        font-weight: 500;
        color: var(--shapetool-section-title-color);
        user-select: none;
        letter-spacing: 1.2px;
    }
    .content {
        display: flex;
        flex-direction: column;
        width: 100%;
    }
</style>

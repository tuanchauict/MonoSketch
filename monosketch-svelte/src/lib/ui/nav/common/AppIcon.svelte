<script lang="ts">
    export let viewBoxSize: number = 24;
    export let size: number = 24;
    export let fill: string = 'currentColor';

    export let onClick: (e: MouseEvent) => void = () => {};
</script>
<button on:click={onClick}>
    <svg xmlns="http://www.w3.org/2000/svg"
         viewBox="0 0 {viewBoxSize} {viewBoxSize}" fill="{fill}" width="{size}" height="{size}">
        <slot/>
    </svg>
</button>

<style lang="scss">
    button {
        display: flex;
        justify-content: center;
        align-items: center;
        border-radius: 50%;
        width: 34px;
        height: 34px;
        cursor: pointer;
        background: #0000;
        border: none;
        color: inherit;

        &:hover {
            background: var(--nav-action-hover-bg);
        }
    }
</style>

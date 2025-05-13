<script lang="ts">
import SvgIcon from '../../../common/SvgIcon.svelte';

export let deleting: boolean;
export let opening: boolean;
</script>

{#if deleting}
    <SvgIcon size="{16}" viewBoxSize="{512}">
        <path
            d="M256 32c14 0 27 8 35 20l216 368a40 40 0 0 1-35 60H40a40 40 0 0 1-34-60L222 52c7-12 20-20 34-20zm0 128c-13 0-24 11-24 24v112a24 24 0 1 0 48 0V184c0-13-11-24-24-24zm32 224a32 32 0 1 0-64 0 32 32 0 1 0 64 0z"
        ></path>
    </SvgIcon>
{:else if opening}
    <SvgIcon size="{16}" viewBoxSize="{512}">
        <path
            d="M384 480h48c11 0 22-6 28-16l112-192a32 32 0 0 0-28-48H144c-11 0-22 6-28 16L48 357V96c0-9 7-16 16-16h118c4 0 8 2 11 5l26 26c21 21 50 33 80 33h117c9 0 16 7 16 16v32h48v-32c0-35-29-64-64-64H299c-17 0-34-7-46-19l-26-26a64 64 0 0 0-46-19H64C29 32 0 61 0 96v320c0 35 29 64 64 64h320z"
        ></path>
    </SvgIcon>
{:else}
    <SvgIcon size="{16}" viewBoxSize="{512}">
        <path
            d="M64 480h384c35 0 64-29 64-64V160c0-35-29-64-64-64H288c-10 0-20-5-26-13l-19-25a64 64 0 0 0-51-26H64C29 32 0 61 0 96v320c0 35 29 64 64 64z"
        ></path>
    </SvgIcon>
{/if}

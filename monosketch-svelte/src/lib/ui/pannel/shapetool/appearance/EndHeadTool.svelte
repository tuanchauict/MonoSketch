<script lang="ts">
import CommonLineAnchorTool from './common/CommonLineAnchorTool.svelte';

let selectedId: string = 'A0';
</script>

<CommonLineAnchorTool title="End head" bind:selectedId />

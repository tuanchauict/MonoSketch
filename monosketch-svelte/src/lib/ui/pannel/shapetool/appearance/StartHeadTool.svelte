<script lang="ts">
import CommonLineAnchorTool from './common/CommonLineAnchorTool.svelte';

let selectedId: string = 'A0';

$: console.log(selectedId);
</script>

<CommonLineAnchorTool title="Start head" bind:selectedId />

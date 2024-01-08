<script lang="ts">
import FileRow from './FileRow.svelte';

let fileList = ["File 1", "File 2", "File 3", "File 4", "File 5", "File 6"]

</script>

<div>
    {#each fileList as file}
        <FileRow name={file} opening="{false}"/>
    {/each}
</div>

<style lang="scss">
    div {
        display: flex;
        flex-direction: column;
        width: 100%;
        height: 100%;
        max-height: 200px;
        min-height: 50px;
        overflow-y: auto;
    }
</style>

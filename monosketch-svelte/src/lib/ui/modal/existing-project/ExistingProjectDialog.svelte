<!--
  - Copyright (c) 2024, tuanchauict
  -->

<script lang="ts">

    import type { ExistingProjectModel } from "$ui/modal/existing-project/model";
    import Dialog from "$ui/modal/common/Dialog.svelte";
    import { modalViewModel } from "$ui/modal/viewmodel";

    export let model: ExistingProjectModel;

    function handleDismiss() {
        modalViewModel.existingProjectFlow.value = null;
    }

    function createReadableDate(timestamp: number): string {
        const date = new Date(timestamp);
        return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`;
    }
</script>

<Dialog title="Existing project"

        confirmText="Replace"
        onConfirm={() => model.onReplace()}

        cancelText="Keep both"
        onCancel={() => model.onKeepBoth()}

        onDismiss={handleDismiss}
>
    <section>
        <p>Same project id exists in the data store</p>
        <ul>
            <li>Project name: {model.projectName}</li>
            <li>Last edit: {createReadableDate(model.lastEditedTimeMillis)}</li>
        </ul>
    </section>
</Dialog>

<style lang="scss">
section {
    margin: 0 8px;
    color: var(--contentText);
}

ul {
    margin: 0 4px;
    padding: 8px;
}

li:last-child {
    margin-top: 4px;
}
</style>
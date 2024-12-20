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
</script>

<Dialog title="Existing project"
        content="Do you want to open the existing project?"
        confirmText="Replace"
        onConfirm={() => model.onReplace()}
        cancelText="Keep both"
        onCancel={() => model.onKeepBoth()}
        onDismiss={handleDismiss}
>

</Dialog>
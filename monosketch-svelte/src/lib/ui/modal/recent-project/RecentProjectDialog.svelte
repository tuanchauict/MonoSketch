<script lang="ts">
import NoBackgroundModal from '../common/NoBackgroundModal.svelte';
import FilterInput from './FilterInput.svelte';
import TopActions from './top-actions/TopActions.svelte';
import FileList from './file-list/FileList.svelte';
import { modalViewModel } from '../viewmodel';

const widthPx = '500px';
const style = `top: 0; left: calc(50% - ${widthPx} / 2); width: ${widthPx};`;

let filterValue = '';

function onDismiss() {
    modalViewModel.projectManagementVisibilityStateFlow.value = false;
}
</script>

<NoBackgroundModal {onDismiss} {style}>
    <div class="container">
        <FilterInput bind:value="{filterValue}" />
        <TopActions dismiss="{onDismiss}" />
        <FileList dismiss="{onDismiss}" />
    </div>
</NoBackgroundModal>

<style lang="scss">
.container {
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    flex-grow: 0;
    width: 100%;
    margin-top: 6px;
    padding: 8px 0;

    border-radius: 6px;
    border: 1px solid var(--mainmenu-divider-color);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.25);
    background: var(--modal-container-bg);
}
</style>

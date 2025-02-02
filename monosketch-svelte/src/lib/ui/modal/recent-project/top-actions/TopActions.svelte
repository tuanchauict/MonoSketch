<script lang="ts">
import MenuItem from '../common/MenuItem.svelte';
import SvgIcon from '../../../common/SvgIcon.svelte';
import { projectDataViewModel } from '$ui/modal/recent-project/project-data-viewmodel';

export let dismiss: () => void;

function createNewProject() {
    projectDataViewModel.newProject();
}

function importFromFile() {
    console.log('importFromFile');
}
</script>

<div>
    <MenuItem onClick="{createNewProject}" {dismiss}>
        <SvgIcon slot="icon" size="{16}" viewBoxSize="{[384, 512]}">
            <path
                d="M0 64C0 29 29 0 64 0h160v128c0 18 14 32 32 32h128v288c0 35-29 64-64 64H64c-35 0-64-29-64-64V64zm384 64H256V0l128 128z"
            ></path>
        </SvgIcon>
        <span slot="content">New project</span>
    </MenuItem>
    <MenuItem onClick="{importFromFile}" {dismiss}>
        <SvgIcon slot="icon" size="{16}" viewBoxSize="{512}">
            <path
                d="M288 109v243a32 32 0 1 1-64 0V109l-73 74a32 32 0 0 1-46-46L233 9c13-12 33-12 46 0l128 128a32 32 0 0 1-46 46l-73-74zM64 352h128a64 64 0 0 0 128 0h128c35 0 64 29 64 64v32c0 35-29 64-64 64H64c-35 0-64-29-64-64v-32c0-35 29-64 64-64zm368 104a24 24 0 1 0 0-48 24 24 0 1 0 0 48z"
            ></path>
        </SvgIcon>
        <span slot="content">Import from file</span>
    </MenuItem>
</div>

<style lang="scss">
div {
    border-bottom: 1px solid var(--mainmenu-divider-color);
    padding-bottom: 8px;
}
</style>

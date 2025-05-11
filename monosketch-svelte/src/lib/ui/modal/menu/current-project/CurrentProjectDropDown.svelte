<script lang="ts">
    import DropDown from '../common/DropDown.svelte';
    import MenuItem from '../common/MenuItem.svelte';
    import type { Rect } from "$libs/graphics-geo/rect";
    import { getProjectDataViewModel } from "$ui/nav/project/constants";

    export let projectId: string;
    export let targetBounds: Rect;
    export let onDismiss: () => void;

    const projectDataViewModel = getProjectDataViewModel();

    function rename() {
        projectDataViewModel.setRenamingProject(projectId);
    }

    function saveAs() {
        projectDataViewModel.saveCurrentShapesToFile();
    }

    function exportText() {
        projectDataViewModel.exportSelectedShapes();
    }
</script>

<DropDown targetBounds="{targetBounds}" {onDismiss} verticalOffsetPx="{6}">
    <MenuItem title="Rename" onClick="{rename}" dismiss="{onDismiss}"/>
    <MenuItem title="Save As" onClick="{saveAs}" dismiss="{onDismiss}"/>
    <MenuItem title="Export Text" onClick="{exportText}" dismiss="{onDismiss}"/>
</DropDown>

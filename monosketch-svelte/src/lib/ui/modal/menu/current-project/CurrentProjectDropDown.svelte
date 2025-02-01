<script lang="ts">
    import DropDown from '../common/DropDown.svelte';
    import MenuItem from '../common/MenuItem.svelte';
    import { modalViewModel } from '../../viewmodel';
    import type { Rect } from "$libs/graphics-geo/rect";
    import { CurrentProjectMenuAction } from "$ui/modal/menu/current-project/model";

    export let projectId: string;
    export let targetBounds: Rect;
    export let onDismiss: () => void;
    export let onAction: (action: CurrentProjectMenuAction) => void;

    function rename() {
        modalViewModel.renamingProjectModalStateFlow.value = {
            id: projectId,
            targetBounds: targetBounds,
        };
        onAction(CurrentProjectMenuAction.RENAME);
    }

    function saveAs() {
        console.log('saveAs');
        onAction(CurrentProjectMenuAction.SAVE_AS);
    }

    function exportText() {
        console.log('exportText');
        onAction(CurrentProjectMenuAction.EXPORT);
    }
</script>

<DropDown targetBounds="{targetBounds}" {onDismiss} verticalOffsetPx="{6}">
    <MenuItem title="Rename" onClick="{rename}" dismiss="{onDismiss}"/>
    <MenuItem title="Save As" onClick="{saveAs}" dismiss="{onDismiss}"/>
    <MenuItem title="Export Text" onClick="{exportText}" dismiss="{onDismiss}"/>
</DropDown>

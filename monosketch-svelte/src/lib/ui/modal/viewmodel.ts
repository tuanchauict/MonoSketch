import { Flow } from '$libs/flow';
import type { Tooltip } from './tooltip/model';
import type { RenameProjectModel } from './rename-project/model';
import type { CurrentProjectModel } from './menu/current-project/model';
import type { Rect } from '$libs/graphics-geo/rect';

class ModalViewModel {
    mainDropDownMenuTargetFlow: Flow<Rect | null> = new Flow(null);
    currentProjectDropDownMenuTargetFlow: Flow<CurrentProjectModel | null> = new Flow(null);

    projectManagementVisibilityStateFlow: Flow<boolean> = new Flow(false);
    renamingProjectModalStateFlow: Flow<RenameProjectModel | null> = new Flow(null);

    tooltipFlow: Flow<Tooltip | null> = new Flow(null);

    keyboardShortcutVisibilityStateFlow: Flow<boolean> = new Flow(false);
}

export const modalViewModel = new ModalViewModel();

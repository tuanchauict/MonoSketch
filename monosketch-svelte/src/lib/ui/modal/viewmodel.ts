import { Flow } from '../../mono/flow';
import type { TargetBounds } from './model';
import type { Tooltip } from './tooltip/model';
import type { RenameProjectModel } from './rename-project/model';
import type { CurrentProjectModel } from './menu/current-project/model';

class ModalViewModel {
    mainDropDownMenuTargetFlow: Flow<TargetBounds | null> = new Flow(null);
    currentProjectDropDownMenuTargetFlow: Flow<CurrentProjectModel | null> = new Flow(null);

    projectManagementVisibilityStateFlow: Flow<boolean> = new Flow(false);
    renamingProjectModalStateFlow: Flow<RenameProjectModel | null> = new Flow(null);

    tooltipFlow: Flow<Tooltip | null> = new Flow(null);

    keyboardShortcutVisibilityStateFlow: Flow<boolean> = new Flow(false);
}

export const modalViewModel = new ModalViewModel();

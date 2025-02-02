import { Flow } from '$libs/flow';
import type { ExistingProjectModel } from "$ui/modal/existing-project/model";

class ModalViewModel {
    keyboardShortcutVisibilityStateFlow: Flow<boolean> = new Flow(false);

    existingProjectFlow: Flow<ExistingProjectModel | null> = new Flow(null);
}

export const modalViewModel = new ModalViewModel();

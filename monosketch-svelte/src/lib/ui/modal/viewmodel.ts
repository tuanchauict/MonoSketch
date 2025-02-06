import { Flow } from '$libs/flow';

class ModalViewModel {
    keyboardShortcutVisibilityStateFlow: Flow<boolean> = new Flow(false);
}

export const modalViewModel = new ModalViewModel();

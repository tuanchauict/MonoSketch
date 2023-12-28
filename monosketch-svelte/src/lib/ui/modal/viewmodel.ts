import {Flow} from '../../mono/flow';

class ModalViewModel {
    mainDropDownMenuTargetFlow: Flow<HTMLElement|null> = new Flow();
}

export const modalViewModel = new ModalViewModel();

import {Flow} from '../../mono/flow';
import type {TargetBounds} from "./model";

class ModalViewModel {
    mainDropDownMenuTargetFlow: Flow<TargetBounds|null> = new Flow();
}

export const modalViewModel = new ModalViewModel();

import { Flow } from '../../mono/flow';
import type { TargetBounds } from './model';
import type { Tooltip } from './tooltip/model';

class ModalViewModel {
    mainDropDownMenuTargetFlow: Flow<TargetBounds | null> = new Flow(null);

    tooltipFlow: Flow<Tooltip | null> = new Flow(null);
}

export const modalViewModel = new ModalViewModel();

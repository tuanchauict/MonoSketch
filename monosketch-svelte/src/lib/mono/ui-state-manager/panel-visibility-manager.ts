import { Flow } from 'lib/libs/flow';

export class PanelVisibilityManager {
    private shapeFormatPanelVisibilityMutableFlow = new Flow<boolean>(true);
    shapeFormatPanelVisibilityFlow = this.shapeFormatPanelVisibilityMutableFlow.immutable();

    setShapeFormatPanelVisibility(isVisible: boolean): void {
        this.shapeFormatPanelVisibilityMutableFlow.value = isVisible;
    }
}

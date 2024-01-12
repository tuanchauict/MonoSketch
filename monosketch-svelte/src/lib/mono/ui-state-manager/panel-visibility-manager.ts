import { Flow } from '$mono/flow';

export class PanelVisibilityManager {
    private _shapeFormatPanelVisibilityFlow = new Flow<boolean>(true);
    shapeFormatPanelVisibilityFlow = this._shapeFormatPanelVisibilityFlow.immutable();

    toggleShapeFormatPanelVisibility(): void {
        this._shapeFormatPanelVisibilityFlow.value = !this._shapeFormatPanelVisibilityFlow.value;
    }
}

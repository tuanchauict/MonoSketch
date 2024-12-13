import type { UiStatePayloadType } from "$mono/ui-state-manager/ui-state-payload";
import { Flow, LifecycleOwner } from 'lib/libs/flow';
import { ScrollMode, type ThemeColor, type ThemeMode } from '$mono/ui-state-manager/states';
import { AppThemeManager } from '$mono/ui-state-manager/theme-manager';
import { ScrollModeManager } from '$mono/ui-state-manager/scroll-mode-manager';
import { PanelVisibilityManager } from '$mono/ui-state-manager/panel-visibility-manager';
import { type KeyCommand, KeyCommandController } from '$mono/keycommand';

/**
 * A domain class for managing UI state of the app.
 * This does not include the state of the workspace.
 */
export class AppUiStateManager {
    private appThemeManager = new AppThemeManager();
    private scrollModeManager = new ScrollModeManager();
    private panelVisibilityManager = new PanelVisibilityManager();
    private keyCommandController = new KeyCommandController(document.body);

    themeModeFlow: Flow<ThemeMode> = this.appThemeManager.themeModeFlow;
    scrollModeFlow: Flow<ScrollMode> = this.scrollModeManager.scrollModeFlow;
    shapeFormatPanelVisibilityFlow: Flow<boolean> =
        this.panelVisibilityManager.shapeFormatPanelVisibilityFlow;
    keyCommandFlow: Flow<KeyCommand> = this.keyCommandController.keyCommandFlow;

    constructor(private appLifecycleOwner: LifecycleOwner) {}

    observeTheme = (onThemeChange: () => void): void => {
        this.appThemeManager.observeTheme(this.appLifecycleOwner, onThemeChange);
    };

    getThemedColorCode = (color: ThemeColor): string => {
        return this.appThemeManager.getThemedColorCode(color);
    };

    updateUiState = (payload: UiStatePayloadType): void => {
        switch (payload.type) {
            case 'ShapeToolVisibility':
                this.panelVisibilityManager.setShapeFormatPanelVisibility(payload.isVisible);
                break;
            case 'ChangeScrollMode':
                this.scrollModeManager.setScrollMode(payload.scrollMode);
                break;
            case 'ChangeTheme':
                this.appThemeManager.setTheme(payload.themeMode);
                break;
            case 'ChangeFontSize':
                // this.appThemeManager.changeFontSize(payload.isIncreased);
                break;
        }
    }
}

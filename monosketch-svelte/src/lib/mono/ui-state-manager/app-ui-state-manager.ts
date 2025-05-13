import { DEBUG_MODE } from "$mono/build_environment";
import { StorageDocument, StoreKeys } from "$mono/store-manager";
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
    private settingDocument: StorageDocument = StorageDocument.get(StoreKeys.SETTINGS);

    themeModeFlow: Flow<ThemeMode> = this.appThemeManager.themeModeFlow;
    scrollModeFlow: Flow<ScrollMode> = this.scrollModeManager.scrollModeFlow;
    shapeFormatPanelVisibilityFlow: Flow<boolean> =
        this.panelVisibilityManager.shapeFormatPanelVisibilityFlow;
    keyCommandFlow: Flow<KeyCommand> = this.keyCommandController.keyCommandFlow;

    private fontSizeMutableFlow = new Flow<number>(parseInt(this.settingDocument.get(StoreKeys.FONT_SIZE, "13")!));
    fontSizeFlow: Flow<number> = this.fontSizeMutableFlow.distinctUntilChanged();

    /**
     * A flow that emits true when the font is ready to use.
     */
    private fontReadyMutableFlow = new Flow<boolean>(false);
    fontReadyFlow: Flow<boolean> = this.fontReadyMutableFlow.immutable();

    constructor(
        private appLifecycleOwner: LifecycleOwner,
    ) {
        const workspaceFont = new FontFace(
            'JetBrainsMono-Regular',
            `url('/fonts/JetBrainsMono-Regular.woff2')`,
        );
        workspaceFont.load().then(() => {
            document.fonts.add(workspaceFont);
            this.fontReadyMutableFlow.value = true;
            if (DEBUG_MODE) {
                console.log('Font loaded');
            }
        });
    }

    observeTheme = (): void => {
        this.appThemeManager.observeTheme(this.appLifecycleOwner);
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
                this.changeFontSize(payload.isIncreased);
                break;
        }
    };

    private changeFontSize = (isIncreased: boolean): void => {
        const offset = isIncreased ? 2 : -2;
        const currentFontSize = this.fontSizeMutableFlow.value!;
        const newFontSize = currentFontSize + offset;
        const coercedFontSize = Math.max(13, Math.min(25, newFontSize));
        this.fontSizeMutableFlow.value = coercedFontSize;

        this.settingDocument.set(StoreKeys.FONT_SIZE, coercedFontSize.toString());
    };
}

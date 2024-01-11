import { Flow, LifecycleOwner } from '$mono/flow';
import { AppThemeManager } from './app-theme-manager';
import type { ThemeMode } from '$mono/theme';

/**
 * A domain class for managing UI state of the app.
 * This does not include the state of the workspace.
 */
export class AppUiStateManager {
    private appThemeManager = new AppThemeManager();

    themeModeFlow: Flow<ThemeMode> = this.appThemeManager.themeModeFlow;

    constructor(private appLifecycleOwner: LifecycleOwner) {}

    observeTheme = (onThemeChange: () => void): void => {
        this.appThemeManager.observeTheme(this.appLifecycleOwner, onThemeChange);
    }

    setTheme = (themeMode: ThemeMode): void => {
        this.appThemeManager.setTheme(themeMode);
    }
}

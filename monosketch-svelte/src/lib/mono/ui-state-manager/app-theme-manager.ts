import { ThemeManager, ThemeMode } from '$mono/theme';
import { StorageDocument, StoreKeys } from '$mono/store-manager';
import { Flow, LifecycleOwner } from '$mono/flow';

/**
 * A class for managing theme of the app, including observing theme change from
 * the settings.
 */
export class AppThemeManager {
    private settingsDocument: StorageDocument = StorageDocument.get(StoreKeys.SETTINGS);
    private themeManager: ThemeManager = ThemeManager.getInstance();

    themeModeFlow: Flow<ThemeMode> = this.themeManager.themeModeFlow;

    constructor() {
        const themeMode = this.settingsDocument.get(StoreKeys.THEME_MODE);
        if (themeMode) {
            this.themeManager.setTheme(themeMode as ThemeMode);
        }
    }

    setTheme = (themeMode: ThemeMode): void => {
        this.themeManager.setTheme(themeMode);
    };

    observeTheme = (appLifecycleOwner: LifecycleOwner, onThemeChange: () => void): void => {
        this.themeManager.themeModeFlow.observe(appLifecycleOwner, (themeMode) => {
            document.documentElement.className = themeMode;
            onThemeChange();

            this.settingsDocument.set(StoreKeys.THEME_MODE, themeMode);
        });

        this.settingsDocument.setObserver(StoreKeys.THEME_MODE, {
            onChange: (key, oldValue, newValue) => {
                if (newValue) {
                    this.themeManager.setTheme(newValue as ThemeMode);
                }
            },
        });
    };
}

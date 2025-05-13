import { Flow, LifecycleOwner } from 'lib/libs/flow';
import { type ThemeColor, ThemeMode } from '$mono/ui-state-manager/states';
import { StorageDocument, StoreKeys } from '$mono/store-manager';

/**
 * A class for managing the theme of the app.
 */
export class ThemeManager {
    private static instance: ThemeManager | null = null;

    static getInstance(): ThemeManager {
        if (!ThemeManager.instance) {
            ThemeManager.instance = new ThemeManager();
        }
        return ThemeManager.instance;
    }

    private _themeModeFlow: Flow<ThemeMode> = new Flow();
    themeModeFlow = this._themeModeFlow.immutable();

    /**
     * Gets color from [color] based on the current theme.
     * The return is the hex code of RGB or RGBA, the same to the hex code used in CSS.
     */
    getThemedColorCode(color: ThemeColor): string {
        return this._themeModeFlow.value === ThemeMode.DARK
            ? color.darkColorCode
            : color.lightColorCode;
    }

    setTheme(themeMode: ThemeMode): void {
        this._themeModeFlow.value = themeMode;
    }
}

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

    observeTheme = (appLifecycleOwner: LifecycleOwner): void => {
        this.themeManager.themeModeFlow.observe(appLifecycleOwner, (themeMode) => {
            document.documentElement.className = themeMode;
            this.settingsDocument.set(StoreKeys.THEME_MODE, themeMode);
        });

        this.settingsDocument.setObserver(StoreKeys.THEME_MODE, (_key, _oldValue, newValue) => {
            if (newValue) {
                this.themeManager.setTheme(newValue as ThemeMode);
            }
        });
    };

    getThemedColorCode = (color: ThemeColor): string => {
        return this.themeManager.getThemedColorCode(color);
    };
}

package mono.ui.appstate

import mono.lifecycle.LifecycleOwner
import mono.store.manager.StoreManager
import mono.ui.theme.ThemeManager
import org.w3c.dom.Element

/**
 * A class for managing the overall UI state of the app, including theme, visibility of a main
 * component, etc.
 */
class AppUiStateManager(
    private val appLifecycleOwner: LifecycleOwner,
    themeManager: ThemeManager = ThemeManager.getInstance(),
    storageManager: StoreManager = StoreManager.getInstance()
) {
    private val appThemeManager = AppThemeManager(themeManager, storageManager)

    fun observeTheme(
        documentElement: Element,
        forceUiUpdate: () -> Unit
    ) {
        appThemeManager.observeTheme(appLifecycleOwner, documentElement, forceUiUpdate)
    }
}

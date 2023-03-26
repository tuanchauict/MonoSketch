package mono.app

import mono.state.MainStateManager
import mono.ui.theme.ThemeManager
import mono.ui.theme.ThemeMode
import org.w3c.dom.Element

internal class AppThemeManager {
    fun observeTheme(
        application: MonoSketchApplication,
        documentElement: Element,
        mainStateManager: MainStateManager
    ) {
        ThemeManager.getInstance().themeModeLiveData.observe(application) {
            documentElement.className = when (it) {
                ThemeMode.LIGHT -> THEME_LIGHT
                ThemeMode.DARK -> THEME_DARK
            }
            mainStateManager.forceFullyRedrawWorkspace()
            // TODO: Save theme to local storage
        }

        // TODO: Observe theme value in local storage
    }

    companion object {
        private const val THEME_LIGHT = "light"
        private const val THEME_DARK = "dark"
    }
}

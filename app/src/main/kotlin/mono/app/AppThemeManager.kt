package mono.app

import mono.state.MainStateManager
import mono.store.manager.StoreKeys
import mono.store.manager.StoreManager
import mono.ui.theme.ThemeManager
import mono.ui.theme.ThemeMode
import org.w3c.dom.Element

internal class AppThemeManager(
    private val themeManager: ThemeManager,
    private val storeManager: StoreManager
) {
    init {
        val themeMode = storeManager.get(StoreKeys.THEME_MODE)
            ?.let(ThemeMode::valueOf)
            ?: ThemeMode.DARK
        val themeManager = ThemeManager.getInstance()
        themeManager.setTheme(themeMode)
    }

    fun observeTheme(
        application: MonoSketchApplication,
        documentElement: Element,
        mainStateManager: MainStateManager
    ) {

        themeManager.themeModeLiveData.observe(application) {
            documentElement.className = when (it) {
                ThemeMode.LIGHT -> THEME_LIGHT
                ThemeMode.DARK -> THEME_DARK
            }
            mainStateManager.forceFullyRedrawWorkspace()
            storeManager.set(StoreKeys.THEME_MODE, it.name)
        }

        // TODO: Observe theme value in local storage
    }

    companion object {
        private const val THEME_LIGHT = "light"
        private const val THEME_DARK = "dark"
    }
}

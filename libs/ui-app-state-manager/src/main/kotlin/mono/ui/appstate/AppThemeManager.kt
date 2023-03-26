package mono.ui.appstate

import mono.lifecycle.LifecycleOwner
import mono.store.manager.StoreKeys
import mono.store.manager.StoreManager
import mono.ui.theme.ThemeManager
import mono.ui.theme.ThemeMode
import org.w3c.dom.Element

/**
 * A class for managing theme
 */
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
        appLifecycleOwner: LifecycleOwner,
        documentElement: Element,
        forceUiUpdate: () -> Unit
    ) {

        themeManager.themeModeLiveData.observe(appLifecycleOwner) {
            documentElement.className = when (it) {
                ThemeMode.LIGHT -> THEME_LIGHT
                ThemeMode.DARK -> THEME_DARK
            }
            forceUiUpdate()
            storeManager.set(StoreKeys.THEME_MODE, it.name)
        }

        storeManager.setObserver(StoreKeys.THEME_MODE) { _, _, newValue ->
            val themeMode = newValue?.let(ThemeMode::valueOf) ?: ThemeMode.DARK
            themeManager.setTheme(themeMode)
        }
    }

    companion object {
        private const val THEME_LIGHT = "light"
        private const val THEME_DARK = "dark"
    }
}

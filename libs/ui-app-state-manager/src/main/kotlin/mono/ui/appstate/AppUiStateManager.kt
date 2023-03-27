package mono.ui.appstate

import mono.lifecycle.LifecycleOwner
import mono.livedata.MutableLiveData
import mono.livedata.distinctUntilChange
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

    private val shapeToolVisibilityMutableLiveData = MutableLiveData(true)
    val shapeToolVisibilityLiveData = shapeToolVisibilityMutableLiveData.distinctUntilChange()

    fun observeTheme(
        documentElement: Element,
        forceUiUpdate: () -> Unit
    ) {
        appThemeManager.observeTheme(appLifecycleOwner, documentElement, forceUiUpdate)
    }

    fun updateUiState(payload: UiStatePayload) {
        when (payload) {
            is UiStatePayload.ShapeToolVisibility ->
                shapeToolVisibilityMutableLiveData.value = payload.isVisible
        }
    }

    /**
     * An interface for containing the payload of updating UI State.
     */
    sealed interface UiStatePayload {
        class ShapeToolVisibility(val isVisible: Boolean) : UiStatePayload
    }
}

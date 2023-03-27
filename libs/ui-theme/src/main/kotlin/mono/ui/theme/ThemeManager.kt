package mono.ui.theme

import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.distinctUntilChange

/**
 * A class for managing the theme of the app.
 */
class ThemeManager private constructor(initialThemeMode: ThemeMode) {
    private val themeModeMutableLiveData: MutableLiveData<ThemeMode> =
        MutableLiveData(initialThemeMode)

    val themeModeLiveData: LiveData<ThemeMode> = themeModeMutableLiveData.distinctUntilChange()

    /**
     * Gets color from [color] based on the current theme.
     * The return is the hex code of RGB or RGBA, the same to the hex code used in CSS.
     */
    fun getColorCode(color: ThemeColor): String = when (themeModeLiveData.value) {
        ThemeMode.LIGHT -> color.lightColorCode
        ThemeMode.DARK -> color.darkColorCode
    }

    fun setTheme(themeMode: ThemeMode) {
        themeModeMutableLiveData.value = themeMode
    }

    companion object {
        private var instance: ThemeManager? = null

        fun getInstance(): ThemeManager {
            val nonNullInstance = instance ?: ThemeManager(ThemeMode.DARK)
            instance = nonNullInstance
            return nonNullInstance
        }
    }
}

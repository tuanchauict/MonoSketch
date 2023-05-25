/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.nav

import androidx.compose.runtime.Composable
import mono.actionmanager.OneTimeActionType
import mono.html.modal.compose.DropDownItem
import mono.html.modal.compose.DropDownMenu
import mono.html.modal.tooltip
import mono.ui.appstate.AppUiStateManager
import mono.ui.appstate.AppUiStateManager.UiStatePayload.ChangeScrollMode
import mono.ui.appstate.state.ScrollMode
import mono.ui.compose.ext.Svg
import mono.ui.compose.ext.SvgPath
import mono.ui.compose.ext.fill
import mono.ui.compose.ext.size
import mono.ui.compose.ext.viewBox
import mono.ui.theme.ThemeManager
import mono.ui.theme.ThemeMode
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

@Composable
internal fun ScrollModeButton(
    scrollMode: ScrollMode,
    updateUiState: (AppUiStateManager.UiStatePayload) -> Unit
) {
    val path = when (scrollMode) {
        ScrollMode.BOTH ->
            "M25 20a5 5 0 1 1-10 0 5 5 0 0 1 10 0ZM19.01.582a1.134 1.134 0 0 1 1.98 0l4.87 8.834c.41.744-.133 1.651-.99 1.651h-9.74c-.857 0-1.4-.907-.99-1.651L19.01.582ZM20.99 39.418a1.134 1.134 0 0 1-1.98 0l-4.87-8.834c-.41-.744.133-1.651.99-1.651h9.74c.857 0 1.4.907.99 1.651l-4.87 8.834ZM.582 20.99a1.134 1.134 0 0 1 0-1.98l8.834-4.87c.744-.41 1.651.133 1.651.99v9.74c0 .857-.907 1.4-1.651.99L.582 20.99ZM39.418 19.01a1.134 1.134 0 0 1 0 1.98l-8.834 4.87c-.744.41-1.651-.133-1.651-.99v-9.74c0-.857.907-1.4 1.651-.99l8.834 4.87Z" // ktlint-disable max-line-length
        ScrollMode.VERTICAL ->
            "M25 20a5 5 0 1 1-10 0 5 5 0 0 1 10 0ZM19.01.582a1.134 1.134 0 0 1 1.98 0l4.87 8.834c.41.744-.133 1.651-.99 1.651h-9.74c-.857 0-1.4-.907-.99-1.651L19.01.582ZM20.99 39.418a1.134 1.134 0 0 1-1.98 0l-4.87-8.834c-.41-.744.133-1.651.99-1.651h9.74c.857 0 1.4.907.99 1.651l-4.87 8.834Z" // ktlint-disable max-line-length
        ScrollMode.HORIZONTAL ->
            "M25 20a5 5 0 1 1-10 0 5 5 0 0 1 10 0ZM.582 20.99a1.134 1.134 0 0 1 0-1.98l8.834-4.87c.744-.41 1.651.133 1.651.99v9.74c0 .857-.907 1.4-1.651.99L.582 20.99ZM39.418 19.01a1.134 1.134 0 0 1 0 1.98l-8.834 4.87c-.744.41-1.651-.133-1.651-.99v-9.74c0-.857.907-1.4 1.651-.99l8.834 4.87Z" // ktlint-disable max-line-length
    }
    Div(
        attrs = {
            classes("app-icon")
            tooltip("Scroll mode")

            onClick {
                val nextMode = when (scrollMode) {
                    ScrollMode.BOTH -> ScrollMode.VERTICAL
                    ScrollMode.VERTICAL -> ScrollMode.HORIZONTAL
                    ScrollMode.HORIZONTAL -> ScrollMode.BOTH
                }
                updateUiState(ChangeScrollMode(nextMode))
            }
        }
    ) {
        Svg(
            attrs = {
                size(16, 16)
                viewBox(40, 40)
                fill("currentColor")
            }
        ) { SvgPath(path) }
    }
}

@Composable
internal fun ThemeIcons() {
    // The theme icon visibility is controlled by the global theme mode at root
    // along with the class name (theme-light-mode, theme-dark-mode)
    ThemeIcon(
        "theme-light-mode",
        ThemeMode.LIGHT,
        "Light mode",
        "M12 8a4 4 0 1 1-8 0 4 4 0 0 1 8 0zM8.5 2.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zm0 11a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zm5-5a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zm-11 0a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zm9.743-4.036a.5.5 0 1 1-.707-.707.5.5 0 0 1 .707.707zm-7.779 7.779a.5.5 0 1 1-.707-.707.5.5 0 0 1 .707.707zm7.072 0a.5.5 0 1 1 .707-.707.5.5 0 0 1-.707.707zM3.757 4.464a.5.5 0 1 1 .707-.707.5.5 0 0 1-.707.707z" // ktlint-disable max-line-length
    )
    ThemeIcon(
        "theme-dark-mode",
        ThemeMode.DARK,
        "Dark mode",
        "M8 11a3 3 0 1 1 0-6 3 3 0 0 1 0 6zm0 1a4 4 0 1 0 0-8 4 4 0 0 0 0 8zm.5-9.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zm0 11a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zm5-5a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zm-11 0a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zm9.743-4.036a.5.5 0 1 1-.707-.707.5.5 0 0 1 .707.707zm-7.779 7.779a.5.5 0 1 1-.707-.707.5.5 0 0 1 .707.707zm7.072 0a.5.5 0 1 1 .707-.707.5.5 0 0 1-.707.707zM3.757 4.464a.5.5 0 1 1 .707-.707.5.5 0 0 1-.707.707z" // ktlint-disable max-line-length
    )
}

@Composable
private fun ThemeIcon(
    className: String,
    nextMode: ThemeMode,
    tooltipText: String,
    iconPath: String
) {
    Div(
        attrs = {
            classes("app-icon", className)
            tooltip(tooltipText)

            onClick { ThemeManager.getInstance().setTheme(nextMode) }
        }
    ) {
        Svg(
            attrs = {
                size(24, 24)
                viewBox(16, 16)
                fill("currentColor")
            }
        ) { SvgPath(iconPath) }
    }
}

@Composable
internal fun AppMenuIcon(
    appUiStateManager: AppUiStateManager,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    Div(
        attrs = { classes("app-icon-container") }
    ) {
        Div(
            attrs = {
                classes("app-icon")
                onClick {
                    val target = it.currentTarget.unsafeCast<HTMLElement>()
                    val parentNode = target.parentElement ?: return@onClick
                    showDropDownMenu(appUiStateManager, onActionSelected, parentNode)
                }
            }
        ) {
            Svg(
                attrs = {
                    size(20, 20)
                    viewBox(20, 20)
                    fill("currentColor")
                }
            ) {
                /* ktlint-disable max-line-length */
                SvgPath(
                    "M5.2 9.6C5.2 10.4837 4.48366 11.2 3.6 11.2C2.71634 11.2 2 10.4837 2 9.6C2 8.71634 2.71634 8 3.6 8C4.48366 8 5.2 8.71634 5.2 9.6Z"
                )
                SvgPath(
                    "M11.6 9.6C11.6 10.4837 10.8837 11.2 10 11.2C9.11634 11.2 8.4 10.4837 8.4 9.6C8.4 8.71634 9.11634 8 10 8C10.8837 8 11.6 8.71634 11.6 9.6Z"
                )
                SvgPath(
                    "M18 9.6C18 10.4837 17.2837 11.2 16.4 11.2C15.5163 11.2 14.8 10.4837 14.8 9.6C14.8 8.71634 15.5163 8 16.4 8C17.2837 8 18 8.71634 18 9.6Z"
                )
                /* ktlint-enable max-line-length */
            }
        }
    }
}

private fun showDropDownMenu(
    appUiStateManager: AppUiStateManager,
    onActionSelected: (OneTimeActionType) -> Unit,
    anchor: Element
) {
    val items = listOf(
        DropDownItem.Text(
            "Show Format panel",
            OneTimeActionType.AppSettingAction.ShowFormatPanel
        ) { !appUiStateManager.shapeToolVisibilityLiveData.value },
        DropDownItem.Text(
            "Hide Format panel",
            OneTimeActionType.AppSettingAction.HideFormatPanel
        ) { appUiStateManager.shapeToolVisibilityLiveData.value },
        DropDownItem.Text(
            "Keyboard shortcuts",
            OneTimeActionType.AppSettingAction.ShowKeyboardShortcuts
        )
    )
    DropDownMenu(anchor, items) {
        val textItem = it as DropDownItem.Text
        onActionSelected(textItem.key as OneTimeActionType)
    }
}

/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.nav

import mono.actionmanager.OneTimeActionType
import mono.html.Div
import mono.html.SvgIcon
import mono.html.modal.DropDownMenu
import mono.html.modal.DropDownMenu.Item.Text
import mono.html.modal.TooltipPosition
import mono.html.modal.tooltip
import mono.html.setAttributes
import mono.html.setOnClickListener
import mono.html.style
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.ui.appstate.AppUiStateManager
import mono.ui.appstate.state.ScrollMode
import mono.ui.theme.ThemeManager
import mono.ui.theme.ThemeMode
import org.w3c.dom.Element

/**
 * A function to create right toolbar UI.
 */
internal fun Element.RightToolbar(
    lifecycleOwner: LifecycleOwner,
    appUiStateManager: AppUiStateManager,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    ScrollModeIcon(lifecycleOwner, appUiStateManager.scrollModeLiveData) {
        appUiStateManager.updateUiState(AppUiStateManager.UiStatePayload.ChangeScrollMode(it))
    }
    ThemeIcon {
        ThemeManager.getInstance().setTheme(it)
    }
    DropDownMenuIcon {
        val items = listOf(
            Text("Save As...", OneTimeActionType.SaveShapesAs),
            Text("Open File...", OneTimeActionType.OpenShapes),
            Text("Export Text", OneTimeActionType.ExportSelectedShapes),
            DropDownMenu.Item.Divider(),
            Text(
                "Show Format panel",
                OneTimeActionType.ShowFormatPanel
            ) { !appUiStateManager.shapeToolVisibilityLiveData.value },
            Text(
                "Hide Format panel",
                OneTimeActionType.HideFormatPanel
            ) { appUiStateManager.shapeToolVisibilityLiveData.value },
            Text("Keyboard shortcuts", OneTimeActionType.ShowKeyboardShortcuts)
        )
        DropDownMenu("main-dropdown-menu", items) {
            val textItem = it as Text
            onActionSelected(textItem.key as OneTimeActionType)
        }
    }
}

private fun Element.ScrollModeIcon(
    lifecycleOwner: LifecycleOwner,
    scrollModeLiveData: LiveData<ScrollMode>,
    onClickAction: (ScrollMode) -> Unit
) {
    val container = Div("app-icon") {
        tooltip("Scroll mode", TooltipPosition.BOTTOM)
    }
    val scrollBothIcon = container.SvgIcon(
        width = 16,
        height = 16,
        viewPortWidth = 40,
        viewPortHeight = 40,
        "M25 20a5 5 0 1 1-10 0 5 5 0 0 1 10 0ZM19.01.582a1.134 1.134 0 0 1 1.98 0l4.87 8.834c.41.744-.133 1.651-.99 1.651h-9.74c-.857 0-1.4-.907-.99-1.651L19.01.582ZM20.99 39.418a1.134 1.134 0 0 1-1.98 0l-4.87-8.834c-.41-.744.133-1.651.99-1.651h9.74c.857 0 1.4.907.99 1.651l-4.87 8.834ZM.582 20.99a1.134 1.134 0 0 1 0-1.98l8.834-4.87c.744-.41 1.651.133 1.651.99v9.74c0 .857-.907 1.4-1.651.99L.582 20.99ZM39.418 19.01a1.134 1.134 0 0 1 0 1.98l-8.834 4.87c-.744.41-1.651-.133-1.651-.99v-9.74c0-.857.907-1.4 1.651-.99l8.834 4.87Z" // ktlint-disable max-line-length
    )

    val scrollVerticalIcon = container.SvgIcon(
        width = 16,
        height = 16,
        viewPortWidth = 40,
        viewPortHeight = 40,
        "M25 20a5 5 0 1 1-10 0 5 5 0 0 1 10 0ZM19.01.582a1.134 1.134 0 0 1 1.98 0l4.87 8.834c.41.744-.133 1.651-.99 1.651h-9.74c-.857 0-1.4-.907-.99-1.651L19.01.582ZM20.99 39.418a1.134 1.134 0 0 1-1.98 0l-4.87-8.834c-.41-.744.133-1.651.99-1.651h9.74c.857 0 1.4.907.99 1.651l-4.87 8.834Z" // ktlint-disable max-line-length
    )

    val scrollHorizontalIcon = container.SvgIcon(
        width = 16,
        height = 16,
        viewPortWidth = 40,
        viewPortHeight = 40,
        "M25 20a5 5 0 1 1-10 0 5 5 0 0 1 10 0ZM.582 20.99a1.134 1.134 0 0 1 0-1.98l8.834-4.87c.744-.41 1.651.133 1.651.99v9.74c0 .857-.907 1.4-1.651.99L.582 20.99ZM39.418 19.01a1.134 1.134 0 0 1 0 1.98l-8.834 4.87c-.744.41-1.651-.133-1.651-.99v-9.74c0-.857.907-1.4 1.651-.99l8.834 4.87Z" // ktlint-disable max-line-length
    )

    val icons = mapOf(
        ScrollMode.BOTH to scrollBothIcon,
        ScrollMode.VERTICAL to scrollVerticalIcon,
        ScrollMode.HORIZONTAL to scrollHorizontalIcon
    )

    container.setOnClickListener {
        val nextMode = when (scrollModeLiveData.value) {
            ScrollMode.BOTH -> ScrollMode.VERTICAL
            ScrollMode.VERTICAL -> ScrollMode.HORIZONTAL
            ScrollMode.HORIZONTAL -> ScrollMode.BOTH
        }
        onClickAction(nextMode)
    }

    scrollModeLiveData.observe(lifecycleOwner) { scrollMode ->
        icons.values.forEach { it.style("display" to "none") }
        icons[scrollMode]?.style("display" to "block")
    }
}

private fun Element.ThemeIcon(onClickAction: (ThemeMode) -> Unit) {
    // The theme icon visibility is controlled by the global theme mode at root.
    Div("app-icon theme-dark-mode") {
        SvgIcon(
            width = 24,
            height = 24,
            viewPortWidth = 16,
            viewPortHeight = 16,
            "M8 11a3 3 0 1 1 0-6 3 3 0 0 1 0 6zm0 1a4 4 0 1 0 0-8 4 4 0 0 0 0 8zm.5-9.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zm0 11a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zm5-5a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zm-11 0a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zm9.743-4.036a.5.5 0 1 1-.707-.707.5.5 0 0 1 .707.707zm-7.779 7.779a.5.5 0 1 1-.707-.707.5.5 0 0 1 .707.707zm7.072 0a.5.5 0 1 1 .707-.707.5.5 0 0 1-.707.707zM3.757 4.464a.5.5 0 1 1 .707-.707.5.5 0 0 1-.707.707z" // ktlint-disable max-line-length
        )

        setOnClickListener {
            onClickAction(ThemeMode.DARK)
        }

        tooltip("Dark mode", TooltipPosition.BOTTOM)
    }
    Div("app-icon theme-light-mode theme-icon") {
        SvgIcon(
            width = 24,
            height = 24,
            viewPortWidth = 16,
            viewPortHeight = 16,
            "M12 8a4 4 0 1 1-8 0 4 4 0 0 1 8 0zM8.5 2.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zm0 11a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zm5-5a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zm-11 0a.5.5 0 1 1 0-1 .5.5 0 0 1 0 1zm9.743-4.036a.5.5 0 1 1-.707-.707.5.5 0 0 1 .707.707zm-7.779 7.779a.5.5 0 1 1-.707-.707.5.5 0 0 1 .707.707zm7.072 0a.5.5 0 1 1 .707-.707.5.5 0 0 1-.707.707zM3.757 4.464a.5.5 0 1 1 .707-.707.5.5 0 0 1-.707.707z" // ktlint-disable max-line-length
        )

        setOnClickListener {
            onClickAction(ThemeMode.LIGHT)
        }

        tooltip("Light mode", TooltipPosition.BOTTOM)
    }
}

private fun Element.DropDownMenuIcon(onClickAction: () -> Unit) {
    Div("app-icon") {
        setAttributes("onfocus" to "this.blur()")

        /* ktlint-disable max-line-length */
        SvgIcon(
            width = 20,
            height = 20,
            "M5.2 9.6C5.2 10.4837 4.48366 11.2 3.6 11.2C2.71634 11.2 2 10.4837 2 9.6C2 8.71634 2.71634 8 3.6 8C4.48366 8 5.2 8.71634 5.2 9.6Z",
            "M11.6 9.6C11.6 10.4837 10.8837 11.2 10 11.2C9.11634 11.2 8.4 10.4837 8.4 9.6C8.4 8.71634 9.11634 8 10 8C10.8837 8 11.6 8.71634 11.6 9.6Z",
            "M18 9.6C18 10.4837 17.2837 11.2 16.4 11.2C15.5163 11.2 14.8 10.4837 14.8 9.6C14.8 8.71634 15.5163 8 16.4 8C17.2837 8 18 8.71634 18 9.6Z"
        )
        /* ktlint-enable max-line-length */

        setOnClickListener { onClickAction() }
    }
}

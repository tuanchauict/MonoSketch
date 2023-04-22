/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.nav

import mono.actionmanager.RetainableActionType
import mono.html.Div
import mono.html.SvgIcon
import mono.html.modal.tooltip
import mono.html.setAttributes
import mono.html.setOnClickListener
import mono.html.toolbar.view.utils.CssClass
import mono.html.toolbar.view.utils.bindClass
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

/**
 * A function to create mouse action toolbar UI.
 */
internal fun Element.MouseActionGroup(
    lifecycleOwner: LifecycleOwner,
    retainableActionLiveData: LiveData<RetainableActionType>,
    setRetainableAction: (RetainableActionType) -> Unit
) {
    Div("main-mouse-actions") {
        val actionElements = MouseActionType.values().map {
            MouseActionGroupItem(it, setRetainableAction)
        }

        retainableActionLiveData.observe(lifecycleOwner) {
            val selectedAction = MouseActionType.fromRetainableAction(it)

            for (element in actionElements) {
                element.bindClass(CssClass.SELECTED, element.mouseAction == selectedAction)
            }
        }
    }
}

private enum class MouseActionType(
    val retainableActionType: RetainableActionType,
    val title: String,
    val iconPath: String,
    val isDefaultSelected: Boolean = false
) {
    SELECTION(
        RetainableActionType.IDLE,
        title = "Select (V)",
        iconPath = "M7.436 20.61L7.275 3.914l12.296 11.29-7.165.235-4.97 5.168z",
        isDefaultSelected = true
    ),
    ADD_RECTANGLE(
        RetainableActionType.ADD_RECTANGLE,
        title = "Rectangle (R)",
        iconPath = "M22 19H2V5h20v14zM4 7v10h16V7z"
    ),
    ADD_TEXT(
        RetainableActionType.ADD_TEXT,
        title = "Text (T)",
        iconPath = "M5.635 21v-2h12.731v2zm3.27-4v-1.12h2.005V4.12H7.425l-.39.44v2.58h-1.4" +
            "V3h12.731v4.14h-1.4V4.56l-.39-.44h-3.485v11.76h2.005V17z"
    ),
    ADD_LINE(
        RetainableActionType.ADD_LINE,
        title = "Line (L)",
        iconPath = "M18 15v-2H6v2H0V9h6v2h12V9h6v6z"
    );

    companion object {
        private val RETAINABLE_ACTION_TO_MOUSE_ACTION_MAP =
            values().associateBy { it.retainableActionType }
        private val NAME_TO_MOUSE_ACTION_MAP = values().associateBy { it.name }

        fun fromRetainableAction(retainableActionType: RetainableActionType): MouseActionType? =
            RETAINABLE_ACTION_TO_MOUSE_ACTION_MAP[retainableActionType]

        fun fromName(name: String?): MouseActionType? = NAME_TO_MOUSE_ACTION_MAP[name]
    }
}

private fun Element.MouseActionGroupItem(
    mouseActionType: MouseActionType,
    onClick: (RetainableActionType) -> Unit
): HTMLElement = Div(classes = "action-button") {
    SvgIcon(21, 21, 24, 24, mouseActionType.iconPath)

    bindClass(CssClass.SELECTED, mouseActionType.isDefaultSelected)
    setAttributes(ATTR_ACTION to mouseActionType.name)

    tooltip(mouseActionType.title)

    setOnClickListener { onClick(mouseActionType.retainableActionType) }
}

private val Element.mouseAction: MouseActionType?
    get() = MouseActionType.fromName(getAttribute(ATTR_ACTION))

private const val ATTR_ACTION = "data-action"

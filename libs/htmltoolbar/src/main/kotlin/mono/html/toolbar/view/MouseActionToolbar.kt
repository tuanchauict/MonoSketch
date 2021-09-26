@file:Suppress("FunctionName")

package mono.html.toolbar.view

import mono.html.Div
import mono.html.Input
import mono.html.InputType
import mono.html.Label
import mono.html.SvgPath
import mono.html.modal.tooltip
import mono.html.setAttributes
import mono.html.setOnChangeListener
import mono.html.toolbar.RetainableActionType
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import kotlin.random.Random
import kotlin.random.nextULong

private val random = Random(100)

/**
 * A function to create mouse action toolbar UI.
 */
internal fun Element.MouseActionGroup(
    lifecycleOwner: LifecycleOwner,
    retainableActionLiveData: LiveData<RetainableActionType>,
    setRetainableAction: (RetainableActionType) -> Unit
) {
    val actionTypeToCheckBoxMap = mutableMapOf<RetainableActionType, HTMLInputElement>()
    Div("btn-group btn-group-sm retainable-action-group") {
        setAttributes(
            "role" to "group",
            "aria-label" to "Basic radio toggle button group"
        )

        actionTypeToCheckBoxMap +=
            MouseActionGroupItem(
                MouseActionType.SELECTION,
                onChangeAction = setRetainableAction
            )

        actionTypeToCheckBoxMap +=
            MouseActionGroupItem(
                MouseActionType.ADD_RECTANGLE,
                onChangeAction = setRetainableAction
            )

        actionTypeToCheckBoxMap +=
            MouseActionGroupItem(
                MouseActionType.ADD_TEXT,
                onChangeAction = setRetainableAction
            )

        actionTypeToCheckBoxMap +=
            MouseActionGroupItem(
                MouseActionType.ADD_LINE,
                onChangeAction = setRetainableAction
            )
    }

    retainableActionLiveData.observe(lifecycleOwner) {
        actionTypeToCheckBoxMap[it]?.checked = true
    }
}

private enum class MouseActionType(
    val retainableActionType: RetainableActionType,
    val title: String,
    val isChecked: Boolean,
    private val icon: Element.() -> Unit
) {
    SELECTION(
        RetainableActionType.IDLE,
        "Select (V)",
        isChecked = true,
        {
            SvgIcon(24, 24) {
                SvgPath("M7.436 20.61L7.275 3.914l12.296 11.29-7.165.235-4.97 5.168z")
            }
        }
    ),
    ADD_RECTANGLE(
        RetainableActionType.ADD_RECTANGLE,
        "Rectangle (R)",
        isChecked = false,
        {
            SvgIcon(24, 24) {
                SvgPath("M22 19H2V5h20v14zM4 7v10h16V7z")
            }
        }
    ),
    ADD_TEXT(
        RetainableActionType.ADD_TEXT,
        "Text (T)",
        isChecked = false,
        {
            SvgIcon(24, 24) {
                SvgPath(
                    "M5.635 21v-2h12.731v2zm3.27-4v-1.12h2.005V4.12H7.425l-.39.44v2.58h-1.4" +
                        "V3h12.731v4.14h-1.4V4.56l-.39-.44h-3.485v11.76h2.005V17z"
                )
            }
        }
    ),
    ADD_LINE(
        RetainableActionType.ADD_LINE,
        "Line (L)",
        isChecked = false,
        {
            SvgIcon(24, 24) {
                SvgPath("M18 15v-2H6v2H0V9h6v2h12V9h6v6z")
            }
        }
    );

    fun bindIcon(tagConsumer: Element) {
        with(tagConsumer) {
            icon()
        }
    }
}

private fun Element.MouseActionGroupItem(
    mouseActionType: MouseActionType,
    onChangeAction: (RetainableActionType) -> Unit
): Pair<RetainableActionType, HTMLInputElement> {
    val actionId = "middle-action-${random.nextULong()}"
    val checkbox = Input(InputType.RADIO, "btn-check") {
        id = actionId
        checked = mouseActionType.isChecked
        name = "mouse-action-group"
        setAttributes(
            "autoComplete" to "off",
            // Avoid input being focused which voids key event commands.
            "onfocus" to "this.blur()"
        )
        setOnChangeListener { onChangeAction(mouseActionType.retainableActionType) }
    }
    Label("btn btn-outline-secondary shadow-none icon-action toolbar-btn") {
        setAttributes("for" to actionId)
        tooltip(mouseActionType.title)

        mouseActionType.bindIcon(this)
    }
    return mouseActionType.retainableActionType to checkbox
}

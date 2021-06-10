@file:Suppress("FunctionName", "ClassName")

package mono.html.toolbar

import kotlinx.html.HTMLTag
import kotlinx.html.HtmlBlockInlineTag
import kotlinx.html.InputType
import kotlinx.html.SVG
import kotlinx.html.TagConsumer
import kotlinx.html.a
import kotlinx.html.attributesMapOf
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.js.input
import kotlinx.html.js.label
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.li
import kotlinx.html.svg
import kotlinx.html.ul
import kotlinx.html.visit
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import kotlin.random.Random
import kotlin.random.nextULong

private val random = Random(100)

/**
 * A function to create right toolbar UI.
 */
internal fun TagConsumer<HTMLElement>.RightToolbar(exportSelectedShapes: () -> Unit) {
    val dropdownMenuId = "right-toolbar-dropdown-menu"
    div("toolbar right") {
        div("dropdown") {
            button(classes = "btn btn-outline-primary btn-sm toolbar-btn shadow-none") {
                id = dropdownMenuId
                attributes["data-bs-toggle"] = "dropdown"
                attributes["aria-expanded"] = "false"
                +"•••"
            }
            ul("dropdown-menu dropdown-menu-light") {
                attributes["aria-labelledby"] = dropdownMenuId
                RightToolbarMenuItem("Export", exportSelectedShapes)
            }
        }
    }
}

/**
 * A function to create right toolbar UI.
 */
internal fun TagConsumer<HTMLElement>.MiddleToolbar(
    lifecycleOwner: LifecycleOwner,
    retainableActionLiveData: LiveData<RetainableActionType>,
    setRetainableAction: (RetainableActionType) -> Unit
) {
    div("toolbar middle") {
        MouseActionGroup(lifecycleOwner, retainableActionLiveData, setRetainableAction)
    }
}

private fun TagConsumer<HTMLElement>.RightToolbarMenuItem(
    title: String,
    onClickAction: () -> Unit
) {
    li {
        a(classes = "dropdown-item") {
            onClickFunction = { onClickAction() }
            +title
        }
    }
}

private enum class MouseActionType(
    val retainableActionType: RetainableActionType,
    val title: String,
    val isChecked: Boolean,
    private val icon: TagConsumer<HTMLElement>.() -> Unit
) {
    SELECTION(
        RetainableActionType.IDLE,
        "Select (ESC)",
        isChecked = true,
        {
            SvgIcon(16, 16) {
                SvgPath("M14.082 2.182a.5.5 0 0 1 .103.557L8.528 15.467a.5.5 0 0 1-.917-.007L5.57 10.694.803 8.652a.5.5 0 0 1-.006-.916l12.728-5.657a.5.5 0 0 1 .556.103z")
            }
        }
    ),
    ADD_RECTANGLE(
        RetainableActionType.ADD_RECTANGLE,
        "Rectangle (R)",
        isChecked = false,
        {
            SvgIcon(24, 24) {
                SvgPath("M0 0h24v24H0V0z") {
                    attributes["fill"] = "none"
                }
                SvgPath("M21 6H3v12h18V6zm-2 10H5V8h14v8z")
            }
        }
    ),
    ADD_TEXT(
        RetainableActionType.ADD_TEXT,
        "Text (T)",
        isChecked = false,
        {
            SvgIcon(16, 16) {
                SvgPath("m2.244 13.081.943-2.803H6.66l.944 2.803H8.86L5.54 3.75H4.322L1 13.081h1.244zm2.7-7.923L6.34 9.314H3.51l1.4-4.156h.034zm9.146 7.027h.035v.896h1.128V8.125c0-1.51-1.114-2.345-2.646-2.345-1.736 0-2.59.916-2.666 2.174h1.108c.068-.718.595-1.19 1.517-1.19.971 0 1.518.52 1.518 1.464v.731H12.19c-1.647.007-2.522.8-2.522 2.058 0 1.319.957 2.18 2.345 2.18 1.06 0 1.716-.43 2.078-1.011zm-1.763.035c-.752 0-1.456-.397-1.456-1.244 0-.65.424-1.115 1.408-1.115h1.805v.834c0 .896-.752 1.525-1.757 1.525z")
            }
        }
    ),
    ADD_LINE(
        RetainableActionType.ADD_LINE,
        "Line (L)",
        isChecked = false,
        {
            SvgIcon(24, 24) {
                SvgPath("M19.5 9.5c-1.03 0-1.9.62-2.29 1.5h-2.92c-.39-.88-1.26-1.5-2.29-1.5s-1.9.62-2.29 1.5H6.79c-.39-.88-1.26-1.5-2.29-1.5C3.12 9.5 2 10.62 2 12s1.12 2.5 2.5 2.5c1.03 0 1.9-.62 2.29-1.5h2.92c.39.88 1.26 1.5 2.29 1.5s1.9-.62 2.29-1.5h2.92c.39.88 1.26 1.5 2.29 1.5 1.38 0 2.5-1.12 2.5-2.5s-1.12-2.5-2.5-2.5z")
            }
        }
    );

    fun bindIcon(tagConsumer: TagConsumer<HTMLElement>) {
        with(tagConsumer) {
            icon()
        }
    }
}

private fun TagConsumer<HTMLElement>.MouseActionGroup(
    lifecycleOwner: LifecycleOwner,
    retainableActionLiveData: LiveData<RetainableActionType>,
    setRetainableAction: (RetainableActionType) -> Unit
) {
    val actionTypeToCheckBoxMap = mutableMapOf<RetainableActionType, HTMLInputElement>()
    div("btn-group btn-group-sm retainable-action-group") {
        attributes["role"] = "group"
        attributes["aria-label"] = "Basic radio toggle button group"

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

private fun TagConsumer<HTMLElement>.MouseActionGroupItem(
    mouseActionType: MouseActionType,
    onChangeAction: (RetainableActionType) -> Unit
): Pair<RetainableActionType, HTMLInputElement> {
    val actionId = "middle-action-${random.nextULong()}"
    val checkbox = input(type = InputType.radio, classes = "btn-check") {
        id = actionId
        checked = mouseActionType.isChecked
        autoComplete = false
        name = "mouse-action-group"

        onChangeFunction = { onChangeAction(mouseActionType.retainableActionType) }
    }
    label("btn btn-outline-secondary shadow-none icon-action toolbar-btn") {
        attributes["for"] = actionId
        attributes["title"] = mouseActionType.title

        mouseActionType.bindIcon(this@MouseActionGroupItem)
    }
    return mouseActionType.retainableActionType to checkbox
}

private fun TagConsumer<HTMLElement>.SvgIcon(width: Int, height: Int, pathBlock: SVG.() -> Unit) {
    svg("bi bi-cursor-fill") {
        attributes["width"] = "$width"
        attributes["height"] = "$height"
        attributes["fill"] = "currentColor"
        attributes["viewBox"] = "0 0 $width $height"

        pathBlock()
    }
}

private inline fun TagConsumer<HTMLElement>.SvgPath(
    path: String,
    crossinline block: SVG_PATH.() -> Unit = {}
) = SVG_PATH(attributesMapOf("d", path), this).visit(block)

private class SVG_PATH(
    initialAttributes: Map<String, String>,
    override val consumer: TagConsumer<*>
) :
    HTMLTag(
        tagName = "path",
        consumer,
        initialAttributes,
        namespace = "http://www.w3.org/2000/svg",
        inlineTag = true,
        emptyTag = true
    ),
    HtmlBlockInlineTag

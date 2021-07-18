@file:Suppress("FunctionName")

package mono.html.toolbar.view.shapetool

import kotlinx.html.InputType
import kotlinx.html.js.div
import kotlinx.html.js.input
import kotlinx.html.js.span
import mono.html.toolbar.view.Tag
import mono.html.toolbar.view.shapetool.Class.ADD_RIGHT_SPACE
import mono.html.toolbar.view.shapetool.Class.COLUMN
import mono.html.toolbar.view.shapetool.Class.ICON_BUTTON
import mono.html.toolbar.view.shapetool.Class.INPUT_CHECK_BOX
import mono.html.toolbar.view.shapetool.Class.SMALL
import mono.html.toolbar.view.shapetool.Class.TOOL_TITLE
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

internal abstract class AppearanceSectionViewController(
    rootView: HTMLDivElement
) : ToolViewController(rootView)

private class AppearanceSectionViewControllerImpl(
    rootView: HTMLDivElement,
    private val fillTool: AppearanceToolViewController,
    private val borderTool: AppearanceToolViewController,
    private val strokeTool: AppearanceToolViewController,
    private val startHeadTool: AppearanceToolViewController,
    private val endHeadTool: AppearanceToolViewController
) : AppearanceSectionViewController(rootView)

private class AppearanceToolViewController(
    rootView: HTMLDivElement,
    private val checkBox: HTMLInputElement,
    private val options: List<HTMLElement>
) : ToolViewController(rootView)

internal fun Tag.AppearanceSection(
    fillOptions: List<String> = listOf(" ", "█", "▒", "░", "▚"),
    borderOptions: List<String> = listOf("─", "━", "═"),
    headOptions: List<String> = listOf("▶", "■", "○", "◎", "●")
): AppearanceSectionViewController {
    val tools = mutableListOf<AppearanceToolViewController>()
    val rootView = Section("APPEARANCE") {
        tools += GridTextIconOptions("Fill", fillOptions)
        tools += GridTextIconOptions("Border", borderOptions)
        tools += GridTextIconOptions("Stroke", borderOptions)
        tools += GridTextIconOptions("Start head", headOptions)
        tools += GridTextIconOptions("End head", headOptions)
    }
    return AppearanceSectionViewControllerImpl(
        rootView,
        tools[0],
        tools[1],
        tools[2],
        tools[3],
        tools[4]
    )
}

private fun Tag.GridTextIconOptions(
    title: String,
    options: List<String>
): AppearanceToolViewController {
    var checkBox: HTMLInputElement? = null
    var optionElements: List<HTMLElement>? = null
    val rootView = Tool(hasMoreBottomSpace = true) {
        Row {
            checkBox = CheckColumn()
            optionElements = OptionsColumn(title, options)
        }
    }
    return AppearanceToolViewController(rootView, checkBox!!, optionElements!!)
}

private fun Tag.CheckColumn(): HTMLInputElement {
    var checkBox: HTMLInputElement? = null
    Column(hasMoreRightSpace = true) {
        Row {
            checkBox = input(InputType.checkBox, classes = classes(INPUT_CHECK_BOX))
        }
    }
    return checkBox!!
}

private fun Tag.OptionsColumn(
    title: String,
    options: List<String>
): List<HTMLElement> {
    val optionElements = mutableListOf<HTMLElement>()
    Column {
        Row {
            span(classes(TOOL_TITLE)) { +title }
        }
        Row(isMonoFont = true, isGrid = true) {
            for (option in options) {
                optionElements += span(classes(ICON_BUTTON, SMALL)) { +option }
            }
        }
    }
    return optionElements
}

private fun Tag.Column(hasMoreRightSpace: Boolean = false, block: Tag.() -> Unit) {
    val columnClasses = classes(COLUMN, ADD_RIGHT_SPACE x hasMoreRightSpace)
    div(columnClasses) {
        block()
    }
}

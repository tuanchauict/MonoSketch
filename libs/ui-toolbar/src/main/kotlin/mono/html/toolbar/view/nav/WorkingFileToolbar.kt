/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.nav

import mono.actionmanager.OneTimeActionType
import mono.html.Div
import mono.html.Span
import mono.html.SvgIcon
import mono.html.modal.DropDownMenu
import mono.html.setOnClickListener
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import org.w3c.dom.Element

internal fun Element.WorkingFileToolbar(
    lifecycleOwner: LifecycleOwner,
    filenameLiveData: LiveData<String>,
    onActionSelected: (OneTimeActionType) -> Unit
) {
    Div("working-file-container") {
        Div("divider")

        val fileInfo = Div("file-info") {
            Span("title") {
                filenameLiveData.observe(lifecycleOwner) {
                    innerText = it
                }
            }
            Div("menu-down-icon") {
                SvgIcon(
                    width = 12,
                    height = 12,
                    viewPortWidth = 16,
                    viewPortHeight = 16,
                    "M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z" // ktlint-disable max-line-length
                )
            }
        }

        fileInfo.setOnClickListener { showWorkingFileMenu(fileInfo, onActionSelected) }
    }
}

private fun showWorkingFileMenu(anchor: Element, onActionSelected: (OneTimeActionType) -> Unit) {
    val items = listOf(
        DropDownMenu.Item.Text("New project", OneTimeActionType.NewProject),
        DropDownMenu.Item.Text("Save As...", OneTimeActionType.SaveShapesAs),
        DropDownMenu.Item.Text("Open File...", OneTimeActionType.OpenShapes),
        DropDownMenu.Item.Text("Export Text", OneTimeActionType.ExportSelectedShapes)
    )
    DropDownMenu(items) {
        val textItem = it as DropDownMenu.Item.Text
        onActionSelected(textItem.key as OneTimeActionType)
    }
        .show(anchor)
}

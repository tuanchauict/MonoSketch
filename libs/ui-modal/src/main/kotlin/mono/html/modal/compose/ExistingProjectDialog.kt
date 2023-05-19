/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.modal.compose

import kotlin.js.Date
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.paddingLeft
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul

fun showExitingProjectDialog(
    existingProjectName: String,
    existingProjectLastEditTimeMillis: Double,
    onKeepBothClick: () -> Unit,
    onReplaceClick: () -> Unit
) {
    Dialog(
        title = "Existing project",
        content = {
            val lastEditDate = Date(existingProjectLastEditTimeMillis)
            val readableDate = lastEditDate.run {
                "${getFullYear()}-${getMonth() + 1}-${getDate()} ${getHours()}:${getMinutes()}"
            }
            P(
                attrs = {
                    style {
                        margin(0.px)
                    }
                }
            ) { Text("Same project id exists in the data store") }
            Ul(
                attrs = {
                    style {
                        margin(4.px)
                        paddingLeft(8.px)
                    }
                }
            ) {
                Li { Text("Name: $existingProjectName") }
                Li { Text("Last edit: $readableDate") }
            }
        },
        primaryAction = DialogAction("Replace", isDanger = true, action = onReplaceClick),
        secondaryAction = DialogAction("Keep both", action = onKeepBothClick)
    )
}

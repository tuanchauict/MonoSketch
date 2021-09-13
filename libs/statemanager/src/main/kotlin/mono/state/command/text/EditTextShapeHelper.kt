package mono.state.command.text

import mono.html.modal.EditTextModal
import mono.shape.command.ChangeText
import mono.shape.shape.Text
import mono.state.command.CommandEnvironment

/**
 * A helper class to show edit text dialog for targeted text shape.
 */
internal object EditTextShapeHelper {
    fun showEditTextDialog(
        environment: CommandEnvironment,
        textShape: Text?,
        onFinish: (String) -> Unit = {}
    ) {
        if (textShape == null) {
            return
        }
        val contentBound = textShape.contentBound

        val dialog = EditTextModal(textShape.text) {
            environment.shapeManager.execute(ChangeText(textShape, it))
        }
        dialog.setOnDismiss {
            onFinish(textShape.text)
        }
        dialog.show(
            environment.toXPx(contentBound.left.toDouble()),
            environment.toYPx(contentBound.top.toDouble()),
            environment.toWidthPx(contentBound.width.toDouble()),
            environment.toHeightPx(contentBound.height.toDouble()),
        )
    }
}

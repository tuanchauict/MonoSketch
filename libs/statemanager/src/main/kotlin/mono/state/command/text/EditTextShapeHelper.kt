package mono.state.command.text

import mono.html.modal.EditTextDialog
import mono.shape.ShapeManager
import mono.shape.command.ChangeText
import mono.shape.shape.Text

/**
 * A helper class to show edit text dialog for targeted text shape.
 */
internal object EditTextShapeHelper {
    fun showEditTextDialog(
        shapeManager: ShapeManager,
        textShape: Text?,
        onFinish: () -> Unit = {}
    ) {
        if (textShape == null) {
            return
        }
        val dialog = EditTextDialog("monomodal-mono-edit-text", textShape.text) {
            shapeManager.execute(ChangeText(textShape, it))
        }
        dialog.setOnDismiss(onFinish)
        dialog.show()
    }
}

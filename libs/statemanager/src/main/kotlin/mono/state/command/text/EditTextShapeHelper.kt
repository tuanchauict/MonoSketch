package mono.state.command.text

import mono.html.modal.EditTextDialog
import mono.shape.ShapeManager
import mono.shape.command.ChangeExtra
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
        val dialog = EditTextDialog("monomodal-mono-edit-text", textShape.extra.text) {
            val extraUpdater = Text.Extra.Updater.Text(it)
            shapeManager.execute(ChangeExtra(textShape, extraUpdater))
        }
        dialog.setOnDismiss(onFinish)
        dialog.show()
    }
}

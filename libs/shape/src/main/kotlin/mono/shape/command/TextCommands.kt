package mono.shape.command

import mono.shape.ShapeManager
import mono.shape.shape.Group
import mono.shape.shape.Text

/**
 * A [Command] for changing text for Text shape.
 */
class ChangeText(
    private val target: Text,
    private val newText: String
) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(target.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        val currentVersion = target.versionCode
        target.setText(newText)
        parent.update { currentVersion != target.versionCode }
    }
}

/**
 * A [Command] for making an uneditable text to be editable.
 */
class MakeTextEditable(private val target: Text) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(target.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        val currentVersion = target.versionCode
        target.makeTextEditable()
        parent.update { currentVersion != target.versionCode }
    }
}

/**
 * A [Command] for updating text shape's text editing mode.
 */
class UpdateTextEditingMode(
    private val target: Text,
    private val isEditing: Boolean
) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(target.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        val currentVersion = target.versionCode
        target.setTextEditingMode(isEditing)
        parent.update { currentVersion != target.versionCode }
    }
}

/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.command.text

import mono.graphics.geo.Size
import mono.html.modal.EditTextModal
import mono.shape.command.ChangeBound
import mono.shape.command.ChangeText
import mono.shape.shape.Text
import mono.state.command.CommandEnvironment

/**
 * A helper class to show edit text dialog for targeted text shape.
 */
internal object EditTextShapeHelper {
    fun showEditTextDialog(
        environment: CommandEnvironment,
        textShape: Text,
        isFreeText: Boolean,
        onFinish: (String) -> Unit = {}
    ) {
        val contentBound = textShape.contentBound

        val dialog = EditTextModal(textShape.text) {
            environment.shapeManager.execute(ChangeText(textShape, it))
            if (!isFreeText) {
                environment.adjustNormalTextSize(textShape)
            }
        }
        dialog.setOnDismiss {
            onFinish(textShape.text)
        }
        val contentWidth =
            if (isFreeText) {
                (environment.getWindowBound().width - contentBound.left).coerceAtLeast(30)
            } else {
                contentBound.width
            }
        val contentHeight = if (isFreeText) 4 else contentBound.height
        dialog.show(
            environment.toXPx(contentBound.left.toDouble()),
            environment.toYPx(contentBound.top.toDouble()),
            environment.toWidthPx(contentWidth.toDouble()),
            environment.toHeightPx(contentHeight.toDouble())
        )
    }

    private fun CommandEnvironment.adjustNormalTextSize(text: Text) {
        val newBound = text.bound.copy(size = text.getNormalTextActualSize())
        shapeManager.execute(ChangeBound(text, newBound))
        addSelectedShape(text)
    }

    private fun Text.getNormalTextActualSize(): Size {
        val height = if (extra.hasBorder()) {
            renderableText.getRenderableText().size + 2
        } else {
            renderableText.getRenderableText().size
        }
        val newHeight = if (height > bound.height) height else bound.height
        return Size(bound.width, newHeight)
    }
}

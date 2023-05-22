/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.actionmanager

import mono.shape.command.ChangeOrder
import mono.shape.extra.style.TextAlign
import mono.shape.shape.AbstractShape

/**
 * An enum class which defines all action types which are only have affect once.
 */
sealed interface OneTimeActionType {
    object Idle : OneTimeActionType

    /**
     * A sealed class for actions related to project management
     */
    sealed interface ProjectAction : OneTimeActionType {
        data class RenameCurrentProject(val newName: String) : ProjectAction
        object NewProject : ProjectAction
        object ExportSelectedShapes : ProjectAction
        data class SwitchProject(val projectId: String) : ProjectAction
        data class RemoveProject(val projectId: String) : ProjectAction
        object SaveShapesAs : ProjectAction
        object OpenShapes : ProjectAction
    }

    // Main dropdown menu
    object ShowFormatPanel : OneTimeActionType
    object HideFormatPanel : OneTimeActionType
    object ShowKeyboardShortcuts : OneTimeActionType

    object SelectAllShapes : OneTimeActionType
    object DeselectShapes : OneTimeActionType
    object DeleteSelectedShapes : OneTimeActionType
    object EditSelectedShapes : OneTimeActionType
    data class EditSelectedShape(val shape: AbstractShape?) : OneTimeActionType

    data class TextAlignment(
        val newHorizontalAlign: TextAlign.HorizontalAlign? = null,
        val newVerticalAlign: TextAlign.VerticalAlign? = null
    ) : OneTimeActionType

    data class MoveShapes(val offsetRow: Int, val offsetCol: Int) : OneTimeActionType
    data class ChangeShapeBound(
        val newLeft: Int? = null,
        val newTop: Int? = null,
        val newWidth: Int? = null,
        val newHeight: Int? = null
    ) : OneTimeActionType

    data class ChangeShapeFillExtra(
        val isEnabled: Boolean? = null,
        val newFillStyleId: String? = null
    ) : OneTimeActionType

    data class ChangeShapeBorderExtra(
        val isEnabled: Boolean? = null,
        val newBorderStyleId: String? = null
    ) : OneTimeActionType

    data class ChangeShapeBorderDashPatternExtra(
        val dash: Int?,
        val gap: Int?,
        val offset: Int?
    ) : OneTimeActionType

    data class ChangeLineStrokeExtra(
        val isEnabled: Boolean? = null,
        val newStrokeStyleId: String? = null
    ) : OneTimeActionType

    data class ChangeLineStrokeDashPatternExtra(
        val dash: Int?,
        val gap: Int?,
        val offset: Int?
    ) : OneTimeActionType

    data class ChangeLineStartAnchorExtra(
        val isEnabled: Boolean? = null,
        val newHeadId: String? = null
    ) : OneTimeActionType

    data class ChangeLineEndAnchorExtra(
        val isEnabled: Boolean? = null,
        val newHeadId: String? = null
    ) : OneTimeActionType

    data class ReorderShape(val orderType: ChangeOrder.ChangeOrderType) : OneTimeActionType

    data class Copy(val isRemoveRequired: Boolean) : OneTimeActionType
    object Duplicate : OneTimeActionType

    object CopyText : OneTimeActionType

    object Undo : OneTimeActionType
    object Redo : OneTimeActionType
}

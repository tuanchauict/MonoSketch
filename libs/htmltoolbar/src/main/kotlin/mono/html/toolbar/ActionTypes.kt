package mono.html.toolbar

import mono.shape.command.ChangeOrder
import mono.shape.extra.style.TextAlign
import mono.shape.shape.AbstractShape

/**
 * An enum class which defines all action types which repeatedly have effects after triggered.
 */
enum class RetainableActionType {
    IDLE,
    ADD_RECTANGLE,
    ADD_TEXT,
    ADD_LINE
}

/**
 * An enum class which defines all action types which are only have affect once.
 */
sealed interface OneTimeActionType {
    object Idle : OneTimeActionType

    object SaveShapesAs : OneTimeActionType
    object OpenShapes : OneTimeActionType
    object ExportSelectedShapes : OneTimeActionType

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

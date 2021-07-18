package mono.html.toolbar

import mono.shape.command.ChangeOrder
import mono.shape.shape.extra.TextExtra

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

    data class TextAlignment(
        val newHorizontalAlign: TextExtra.HorizontalAlign? = null,
        val newVerticalAlign: TextExtra.VerticalAlign? = null
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

    data class ReorderShape(val orderType: ChangeOrder.ChangeOrderType) : OneTimeActionType

    data class Copy(val isRemoveRequired: Boolean) : OneTimeActionType
    object Duplicate : OneTimeActionType
}

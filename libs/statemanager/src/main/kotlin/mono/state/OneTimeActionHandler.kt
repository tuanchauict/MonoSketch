package mono.state

import mono.common.exhaustive
import mono.html.toolbar.OneTimeActionType
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.shape.toJson
import mono.state.command.CommandEnvironment

/**
 * A class to handle one time actions.
 */
internal class OneTimeActionHandler(
    lifecycleOwner: LifecycleOwner,
    oneTimeActionLiveData: LiveData<OneTimeActionType>,
    private val environment: CommandEnvironment,
    // TODO: Remove this after moving all required methods into this class
    mainStateManager: MainStateManager
) {

    init {
        oneTimeActionLiveData.observe(lifecycleOwner) {
            when (it) {
                OneTimeActionType.Idle -> Unit

                OneTimeActionType.SaveShapesAs ->
                    mainStateManager.fileMediator.saveFile(environment.shapeManager.toJson(true))
                OneTimeActionType.OpenShapes ->
                    mainStateManager.openSavedFile()
                OneTimeActionType.ExportSelectedShapes ->
                    mainStateManager.exportSelectedShape(true)

                OneTimeActionType.SelectAllShapes ->
                    environment.selectAllShapes()
                OneTimeActionType.DeselectShapes ->
                    environment.clearSelectedShapes()
                OneTimeActionType.DeleteSelectedShapes ->
                    mainStateManager.deleteSelectedShapes()
                OneTimeActionType.EditSelectedShapes ->
                    mainStateManager.editSelectedShape(
                        environment.getSelectedShapes().singleOrNull()
                    )
                is OneTimeActionType.TextAlignment ->
                    mainStateManager.setTextAlignment(it.newHorizontalAlign, it.newVerticalAlign)

                is OneTimeActionType.MoveShapes ->
                    mainStateManager.moveSelectedShapes(it.offsetRow, it.offsetCol)
                is OneTimeActionType.ChangeShapeBound ->
                    mainStateManager.setSelectedShapeBound(
                        it.newLeft,
                        it.newTop,
                        it.newWidth,
                        it.newHeight
                    )

                is OneTimeActionType.ChangeShapeFillExtra ->
                    mainStateManager.setSelectedShapeFillExtra(it.isEnabled, it.newFillStyleId)
                is OneTimeActionType.ChangeShapeBorderExtra ->
                    mainStateManager.setSelectedShapeBorderExtra(it.isEnabled, it.newBorderStyleId)
                is OneTimeActionType.ChangeLineStartAnchorExtra ->
                    mainStateManager.setSelectedShapeStartAnchorExtra(it.isEnabled, it.newHeadId)
                is OneTimeActionType.ChangeLineEndAnchorExtra ->
                    mainStateManager.setSelectedShapeEndAnchorExtra(it.isEnabled, it.newHeadId)

                is OneTimeActionType.ReorderShape ->
                    mainStateManager.changeShapeOrder(it.orderType)

                is OneTimeActionType.Copy ->
                    mainStateManager.clipboardManager.copySelectedShapes(it.isRemoveRequired)
                OneTimeActionType.Duplicate ->
                    mainStateManager.clipboardManager.duplicateSelectedShapes()

                OneTimeActionType.CopyText ->
                    mainStateManager.exportSelectedShape(false)
            }.exhaustive
        }
    }
}

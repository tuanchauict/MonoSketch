package mono.state

import mono.bitmap.manager.MonoBitmapManager
import mono.common.exhaustive
import mono.export.ExportShapesHelper
import mono.html.toolbar.OneTimeActionType
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.replaceWithJson
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
    mainStateManager: MainStateManager,
    bitmapManager: MonoBitmapManager,
    shapeClipboardManager: ShapeClipboardManager,
) {
    private val fileMediator: FileMediator = FileMediator()
    private val exportShapesHelper = ExportShapesHelper(
        bitmapManager::getBitmap,
        shapeClipboardManager::setClipboardText
    )

    init {
        oneTimeActionLiveData.observe(lifecycleOwner) {
            when (it) {
                OneTimeActionType.Idle -> Unit

                OneTimeActionType.SaveShapesAs ->
                    saveCurrentShapesToFile()
                OneTimeActionType.OpenShapes ->
                    loadShapesFromFile()
                OneTimeActionType.ExportSelectedShapes ->
                    exportSelectedShapes(true)

                OneTimeActionType.CopyText ->
                    exportSelectedShapes(false)

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
            }.exhaustive
        }
    }

    private fun saveCurrentShapesToFile() {
        fileMediator.saveFile(environment.shapeManager.toJson(true))
    }

    private fun loadShapesFromFile() {
        fileMediator.openFile { jsonString ->
            environment.shapeManager.replaceWithJson(jsonString)
            environment.workingParentGroup = environment.shapeManager.root
            environment.clearSelectedShapes()
        }
    }

    private fun exportSelectedShapes(isModalRequired: Boolean) {
        val selectedShapes = environment.getSelectedShapes()
        val extractableShapes = when {
            selectedShapes.isNotEmpty() ->
                environment.workingParentGroup.items.filter { it in selectedShapes }
            isModalRequired ->
                listOf(environment.workingParentGroup)
            else ->
                emptyList()
        }

        exportShapesHelper.exportText(extractableShapes, isModalRequired)
    }
}

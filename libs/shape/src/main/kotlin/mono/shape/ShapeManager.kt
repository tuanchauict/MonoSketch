package mono.shape

import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.shape.command.AddShape
import mono.shape.command.Command
import mono.shape.command.GroupShapes
import mono.shape.command.RemoveShape
import mono.shape.command.Ungroup
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group

/**
 * A model class which contains all shapes of the app and also defines all shape handling logics.
 */
class ShapeManager {
    val root: Group = Group(null)
    private val allShapeMap: MutableMap<Int, AbstractShape> = mutableMapOf(root.id to root)

    /**
     * Reflect the version of the root through live data. The other components are able to observe
     * this version to decide update internally.
     */
    private val versionMutableLiveData: MutableLiveData<Int> = MutableLiveData(root.version)
    val versionLiveData: LiveData<Int> = versionMutableLiveData

    /**
     * All shapes of the app which are sorted by drawing layer order.
     */
    val shapes: Collection<AbstractShape> = root.items

    fun execute(command: Command) {
        val affectedParent = command.getDirectAffectedParent(this) ?: return
        val allAncestors = affectedParent.getAllAncestors()
        val currentVersion = affectedParent.version

        command.execute(this, affectedParent)

        if (currentVersion == affectedParent.version && affectedParent.id in allShapeMap) {
            return
        }
        for (parent in allAncestors) {
            parent.update { true }
        }
        versionMutableLiveData.value = root.version
    }

    internal fun getGroup(shapeId: Int?): Group? =
        if (shapeId == null) root else allShapeMap[shapeId] as? Group

    internal fun register(shape: AbstractShape) {
        allShapeMap[shape.id] = shape
    }

    internal fun unregister(shape: AbstractShape) {
        allShapeMap.remove(shape.id)
    }

    private fun Group.getAllAncestors(): List<Group> {
        val result = mutableListOf<Group>()
        var parent = allShapeMap[parentId] as? Group
        while (parent != null) {
            result.add(parent)
            parent = allShapeMap[parent.parentId] as? Group
        }
        return result
    }
}

fun ShapeManager.add(shape: AbstractShape) = execute(AddShape(shape))
fun ShapeManager.remove(shape: AbstractShape) = execute(RemoveShape(shape))
fun ShapeManager.group(sameParentShapes: List<AbstractShape>) =
    execute(GroupShapes(sameParentShapes))

fun ShapeManager.ungroup(group: Group) = execute(Ungroup(group))

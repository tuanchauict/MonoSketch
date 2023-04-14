/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.command

import mono.graphics.geo.Rect
import mono.shape.ShapeManager
import mono.shape.extra.ShapeExtra
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group

class ChangeBound(private val target: AbstractShape, private val newBound: Rect) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(target.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        val currentVersion = target.versionCode
        target.setBound(newBound)
        if (currentVersion != target.versionCode) {
            parent.update { true }
        }
    }
}

class ChangeExtra(
    private val target: AbstractShape,
    private val newExtra: ShapeExtra
) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(target.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        val currentVersion = target.versionCode
        target.setExtra(newExtra)
        if (currentVersion != target.versionCode) {
            parent.update { true }
        }
    }
}

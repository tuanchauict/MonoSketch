/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.command

import mono.shape.ShapeManager
import mono.shape.shape.Group

/**
 * A sealed class which defines common apis for a command. A command must determine direct affected
 * parent group via [getDirectAffectedParent]. If [getDirectAffectedParent] returns null, the
 * command won't be executed.
 */
sealed class Command {
    internal abstract fun getDirectAffectedParent(shapeManager: ShapeManager): Group?

    internal abstract fun execute(shapeManager: ShapeManager, parent: Group)
}

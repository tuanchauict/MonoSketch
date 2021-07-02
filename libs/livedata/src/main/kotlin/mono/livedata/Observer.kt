package mono.livedata

import mono.common.Cancelable
import mono.common.setTimeout

/**
 * Observer interface for notifying change.
 */
interface Observer<T> {
    fun onChanged(newValue: T)
}

internal fun <T> Observer<T>.throttle(durationMillis: Int): Observer<T> =
    if (durationMillis >= 0) ThrottledObserver(durationMillis, this) else this

/**
 * A simple observer which always notify change to [listener] when it receives update.
 */
internal class SimpleObserver<T>(
    private val listener: (T) -> Unit
) : Observer<T> {
    override fun onChanged(newValue: T) {
        listener(newValue)
    }
}

/**
 * An observer which only deliver change within a time window from the first change to a duration
 * from that time. The last value updated will be notify to the observer.
 */
internal class ThrottledObserver<T>(
    private val durationMillis: Int,
    private val observer: Observer<T>
) : Observer<T> {
    private var currentTimeout: Cancelable? = null
    private var currentValue: T? = null

    override fun onChanged(newValue: T) {
        if (currentTimeout == null) {
            currentValue = newValue
            currentTimeout = setTimeout(durationMillis) { timeoutTick() }
        } else {
            currentValue = newValue
        }
    }

    private fun timeoutTick() {
        val newValue = currentValue ?: return
        observer.onChanged(newValue)
        currentTimeout = null
    }
}

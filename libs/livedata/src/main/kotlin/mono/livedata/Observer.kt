package mono.livedata

import mono.common.Timeout
import mono.common.setTimeout

/**
 * Observer interface for notifying change.
 */
interface Observer<T> {
    fun onChanged(oldValue: T, newValue: T)
}

internal fun <T> Observer<T>.distinct(isApplied: Boolean): Observer<T> =
    if (isApplied) DistinctObserver(this) else this

internal fun <T> Observer<T>.throttle(durationMillis: Int): Observer<T> =
    if (durationMillis > 0) ThrottledObserver(durationMillis, this) else this

/**
 * A simple observer which always notify change to [listener] when it receives update.
 */
internal class SimpleObserver<T>(
    private val listener: (T) -> Unit
) : Observer<T> {
    override fun onChanged(oldValue: T, newValue: T) {
        listener(newValue)
    }
}

/**
 * An observer which only notify change when state is changed.
 */
internal class DistinctObserver<T>(private val observer: Observer<T>) : Observer<T> {
    override fun onChanged(oldValue: T, newValue: T) {
        if (oldValue != newValue) {
            observer.onChanged(oldValue, newValue)
        }
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
    private var currentTimeout: Timeout? = null
    private var currentOldValue: T? = null
    private var currentNewValue: T? = null

    override fun onChanged(oldValue: T, newValue: T) {
        if (currentTimeout == null) {
            currentOldValue = oldValue
            currentNewValue = newValue
            currentTimeout = setTimeout(durationMillis) { timeoutTick() }
        } else {
            currentNewValue = newValue
        }
    }

    private fun timeoutTick() {
        val oldValue = currentOldValue ?: return
        val newValue = currentNewValue ?: return
        observer.onChanged(oldValue, newValue)
        currentTimeout = null
    }
}

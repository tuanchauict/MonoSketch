package mono.livedata

import mono.lifecycle.LifecycleObserver
import mono.lifecycle.LifecycleOwner

/**
 * A LiveData like class to observer state of a value
 */
abstract class LiveData<T>(initValue: T) {
    private var valueInternal: T = initValue
    open val value: T
        get() = valueInternal

    private val observers: MutableList<Observer<T>> = mutableListOf()

    /**
     * Observes changes from live data within lifecycle with [lifecycleOwner].
     * Note that if [lifecycleOwner] is stopped, life data won't accept observer.
     */
    fun observe(
        lifecycleOwner: LifecycleOwner,
        isDistinct: Boolean = false,
        throttleDurationMillis: Int = -1,
        listener: (T) -> Unit
    ) {
        if (lifecycleOwner.isStopped) {
            return
        }
        val liveDataObserver =
            SimpleObserver(listener).distinct(isDistinct).throttle(throttleDurationMillis)
        val lifecycleObserver = OnStopLifecycleObserver {
            observers.remove(liveDataObserver)
        }
        observers.add(liveDataObserver)
        lifecycleOwner.addObserver(lifecycleObserver)
    }

    protected fun setValue(value: T) {
        val oldValue = valueInternal
        valueInternal = value

        for (observer in observers) {
            observer.onChanged(oldValue, value)
        }
    }

    private class OnStopLifecycleObserver(private val callback: () -> Unit) : LifecycleObserver {
        override fun onStop() {
            callback()
        }
    }
}

class MutableLiveData<T>(initValue: T) : LiveData<T>(initValue) {
    override var value: T
        get() = super.value
        set(value) = setValue(value)
}

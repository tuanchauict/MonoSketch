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

    // Visible for testing only
    internal val observers: MutableList<Observer<T>> = mutableListOf()

    /**
     * Observes changes from live data within lifecycle with [lifecycleOwner].
     * Note that if [lifecycleOwner] is stopped, life data won't accept observer.
     *
     * @param throttleDurationMillis: Throttling configuration. By default, the throttle duration is
     * set to -1 which means the observer will be executed immediately as the value is updated.
     * With the throttle duration is 0, the execution will be run in the next frame. With throttle
     * duration > 0, it will execute by timeout of the duration with the latest value bound to the
     * live data.
     */
    fun observe(
        lifecycleOwner: LifecycleOwner,
        throttleDurationMillis: Int = -1,
        listener: (T) -> Unit
    ) {
        if (lifecycleOwner.isStopped) {
            return
        }
        val liveDataObserver = SimpleObserver(listener).throttle(throttleDurationMillis)
        val lifecycleObserver = OnStopLifecycleObserver {
            observers.remove(liveDataObserver)
        }
        observers.add(liveDataObserver)
        lifecycleOwner.addObserver(lifecycleObserver)
        liveDataObserver.onChanged(value)
    }

    protected fun setValue(value: T) {
        valueInternal = value

        for (observer in observers) {
            observer.onChanged(value)
        }
    }

    private class OnStopLifecycleObserver(private val callback: () -> Unit) : LifecycleObserver {
        override fun onStop() {
            callback()
        }
    }
}

open class MutableLiveData<T>(initValue: T) : LiveData<T>(initValue) {
    override var value: T
        get() = super.value
        set(value) = setValue(value)
}

class MediatorLiveData<T>(initValue: T) : MutableLiveData<T>(initValue) {
    private val lifecycleOwner: LifecycleOwner = LiveDataLifecycleOwner()

    fun <S> add(liveData: LiveData<S>, transform: MediatorLiveData<T>.(S) -> Unit) {
        liveData.observe(lifecycleOwner) {
            transform(it)
        }
    }
}

private class TransformLiveData<T, R>(
    liveData: LiveData<T>,
    private val transform: (T) -> R
) : LiveData<R>(transform(liveData.value)) {

    init {
        liveData.observe(LiveDataLifecycleOwner()) {
            setValue(transform(it))
        }
    }
}

private class DistinctOnlyLiveData<T>(liveData: LiveData<T>) : LiveData<T>(liveData.value) {
    init {
        liveData.observe(LiveDataLifecycleOwner()) {
            if (it != value) {
                setValue(it)
            }
        }
    }
}

fun <T, R> LiveData<T>.map(transform: (T) -> R): LiveData<R> = TransformLiveData(this, transform)

fun <T> LiveData<T>.distinctUntilChange(): LiveData<T> = DistinctOnlyLiveData(this)

private class LiveDataLifecycleOwner : LifecycleOwner()

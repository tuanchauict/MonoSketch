package mono.livedata

/**
 * A LiveData like class to observer state of a value
 */
abstract class LiveData<T>(initValue: T) {
    private var valueInternal: T = initValue
    open val value: T
        get() = valueInternal

    private val observers: MutableList<Observer<T>> = mutableListOf()

    fun observe(
        isDistinct: Boolean = false,
        throttleDurationMillis: Int = -1,
        listener: (T) -> Unit
    ) {
        observers += SimpleObserver(listener).distinct(isDistinct).throttle(throttleDurationMillis)
    }

    protected fun setValue(value: T) {
        val oldValue = valueInternal
        valueInternal = value

        for (observer in observers) {
            observer.onChanged(oldValue, value)
        }
    }
}

class MutableLiveData<T>(initValue: T) : LiveData<T>(initValue) {
    override var value: T
        get() = super.value
        set(value) = setValue(value)
}

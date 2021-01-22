package mono.lifecycle

interface LifecycleObserver {
    fun onStart() = Unit

    fun onStop() = Unit
}

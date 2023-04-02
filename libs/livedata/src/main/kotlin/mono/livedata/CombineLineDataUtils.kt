/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.livedata

@Suppress("UNCHECKED_CAST")
fun <T1, T2, R> combineLiveData(
    liveData1: LiveData<T1>,
    liveData2: LiveData<T2>,
    conversion: (T1, T2) -> R
): LiveData<R> = combineLiveData(listOf(liveData1, liveData2))
    .map { conversion(it[0] as T1, it[1] as T2) }

fun <T1, T2> combineLiveData(
    liveData1: LiveData<T1>,
    liveData2: LiveData<T2>
): LiveData<Pair<T1, T2>> = combineLiveData(liveData1, liveData2) { t1, t2 -> t1 to t2 }

fun <R> combineLiveData(
    liveData1: LiveData<*>,
    liveData2: LiveData<*>,
    liveData3: LiveData<*>,
    vararg liveDatas: LiveData<*>,
    conversion: (List<*>) -> R
): LiveData<R> =
    combineLiveData(listOf(liveData1, liveData2, liveData3, *liveDatas)).map(conversion)

private fun combineLiveData(liveDatas: List<LiveData<*>>): LiveData<List<*>> {
    val mediatorLiveData = MediatorLiveData(liveDatas)
    for (liveData in liveDatas) {
        mediatorLiveData.add(liveData) {
            value = liveDatas
        }
    }

    return mediatorLiveData.map { sequence -> sequence.map { it.value } }
}

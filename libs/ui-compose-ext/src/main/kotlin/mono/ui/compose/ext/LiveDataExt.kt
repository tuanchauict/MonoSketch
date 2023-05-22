/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.ui.compose.ext

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData

fun <T> LiveData<T>.toState(lifecycleOwner: LifecycleOwner): State<T> {
    val mutableState = mutableStateOf(value)
    observe(lifecycleOwner) {
        mutableState.value = it
    }
    return mutableState
}

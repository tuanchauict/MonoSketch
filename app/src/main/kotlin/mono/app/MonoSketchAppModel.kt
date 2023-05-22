/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.app

import mono.graphics.geo.Size
import mono.livedata.LiveData
import mono.livedata.MutableLiveData

/**
 * A model class for the app-wide states.
 */
class MonoSketchAppModel {
    private val windowSizeMutableLiveData: MutableLiveData<Size> =
        MutableLiveData(Size(0, 0))
    val windowSizeLiveData: LiveData<Size> = windowSizeMutableLiveData

    private val applicationActiveStateMutableLiveData: MutableLiveData<Boolean> =
        MutableLiveData(false)
    val applicationActiveStateLiveData: LiveData<Boolean> = applicationActiveStateMutableLiveData

    fun setWindowSize(size: Size) {
        windowSizeMutableLiveData.value = size
    }

    fun setApplicationActiveState(isActive: Boolean) {
        applicationActiveStateMutableLiveData.value = isActive
    }
}

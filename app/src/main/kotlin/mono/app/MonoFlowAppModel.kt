package mono.app

import mono.graphics.geo.Size
import mono.livedata.LiveData
import mono.livedata.MutableLiveData

class MonoFlowAppModel {
    private val windowSizeMutableLiveData: MutableLiveData<Size> =
        MutableLiveData(Size(0, 0))
    val windowSizeLiveData: LiveData<Size> = windowSizeMutableLiveData

    fun setWindowSize(size: Size) {
        windowSizeMutableLiveData.value = size
    }
}

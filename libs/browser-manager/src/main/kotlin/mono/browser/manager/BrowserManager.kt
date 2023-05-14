/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.browser.manager

import kotlinx.browser.document
import kotlinx.browser.window
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.distinctUntilChange
import mono.store.dao.workspace.WorkspaceDao
import org.w3c.dom.url.URLSearchParams

/**
 * A class for managing the states related to the browser such as the title, address bar, etc.
 */
class BrowserManager {
    private val urlSearchParams: URLSearchParams
        get() = URLSearchParams(window.location.search)

    val rootIdFromUrl
        get() = urlSearchParams.get(URL_PARAM_ID).orEmpty()

    fun startObserveStateChange(
        workingProjectIdLiveData: LiveData<String>,
        lifecycleOwner: LifecycleOwner,
        workspaceDao: WorkspaceDao = WorkspaceDao.instance
    ) {
        workingProjectIdLiveData.observe(lifecycleOwner) {
            document.title = "${workspaceDao.getObject(it).name} - MonoSketch"
        }

        workingProjectIdLiveData.distinctUntilChange().observe(lifecycleOwner) {
            val searchParams = urlSearchParams
            searchParams.set(URL_PARAM_ID, it)
            val newUrl = "${window.location.origin}${window.location.pathname}?$searchParams"
            window.history.pushState(mapOf("path" to newUrl).asDynamic(), "", newUrl)
        }
    }

    companion object {
        private const val URL_PARAM_ID = "id"
    }
}

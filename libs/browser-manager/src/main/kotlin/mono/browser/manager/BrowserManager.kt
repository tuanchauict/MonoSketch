/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.browser.manager

import kotlinx.browser.document
import kotlinx.browser.window
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.store.dao.workspace.WorkspaceDao
import org.w3c.dom.url.URLSearchParams

/**
 * A class for managing the states related to the browser such as the title, address bar, etc.
 */
class BrowserManager(
    workingProjectIdLiveData: LiveData<String>,
    lifecycleOwner: LifecycleOwner,
    workspaceDao: WorkspaceDao = WorkspaceDao.instance
) {
    private val urlSearchParams: URLSearchParams
        get() = URLSearchParams(window.location.search)

    init {
        workingProjectIdLiveData.observe(lifecycleOwner) {
            document.title = "${workspaceDao.getObject(it).name} - MonoSketch"
        }
    }

    fun getInitialRootIdFromUrl(): String = urlSearchParams.get(URL_PARAM_ID).orEmpty()

    companion object {
        private const val URL_PARAM_ID = "id"
    }
}

/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.browser.manager

import kotlinx.browser.document
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.store.dao.workspace.WorkspaceDao

/**
 * A class for managing the states related to the browser such as the title, address bar, etc.
 */
class BrowserManager(
    workingProjectIdLiveData: LiveData<String>,
    lifecycleOwner: LifecycleOwner,
    workspaceDao: WorkspaceDao = WorkspaceDao.instance
) {
    init {
        workingProjectIdLiveData.observe(lifecycleOwner) {
            document.title = "${workspaceDao.getObject(it).name} - MonoSketch"
        }
    }
}

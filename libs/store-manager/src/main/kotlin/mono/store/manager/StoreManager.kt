package mono.store.manager

import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

/**
 * A class for managing storage.
 */
class StoreManager {
    fun set(key: String, json: String) {
        localStorage[key] = json
    }

    fun get(key: String): String? = localStorage[key]

    fun remove(key: String) {
        localStorage.removeItem(key)
    }
}

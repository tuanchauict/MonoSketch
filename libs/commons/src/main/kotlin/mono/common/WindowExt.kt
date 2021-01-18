package mono.common

import kotlinx.browser.window

fun setTimeout(durationMillis: Int, action: () -> Unit): Timeout =
    Timeout(window.setTimeout(action, durationMillis))

fun setInterval(durationMillis: Int, action: () -> Unit): Interval =
    Interval(window.setInterval(action, durationMillis))

class Timeout internal constructor(private val id: Int) {
    fun cancel() = window.clearTimeout(id)
}

class Interval internal constructor(private val id: Int) {
    fun cancel() = window.clearInterval(id)
}

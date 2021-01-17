import kotlinx.browser.window
import mono.app.MonoFlowApplication

fun main() {
    val application = MonoFlowApplication()
    window.onload = {
        application.onStart()
    }
    window.onresize = {
        application.onResize()
    }
}


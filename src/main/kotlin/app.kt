import kotlinx.browser.window
import mono.app.MonoSketchApplication

fun main() {
    val application = MonoSketchApplication()
    window.onload = {
        application.onStart()
    }
    window.onresize = {
        application.onResize()
    }
}

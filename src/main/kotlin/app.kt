import kotlinx.browser.window
import mono.app.MonoFlowApplication

fun main() {
    window.onload = {
        MonoFlowApplication().onStart()
    }
}


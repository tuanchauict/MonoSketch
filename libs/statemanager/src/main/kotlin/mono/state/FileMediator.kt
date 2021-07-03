package mono.state

import kotlinx.browser.document
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.dom.append
import kotlinx.html.js.input
import kotlinx.html.style
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.FileList
import org.w3c.files.FileReader
import org.w3c.files.get

/**
 * A mediator class for file interactions.
 */
internal class FileMediator {
    fun saveFile(jsonString: String) {
        document.body?.append {
            val fileBlob = Blob(arrayOf(jsonString))
            val node = a {
                href = URL.Companion.createObjectURL(fileBlob)
                attributes["download"] = "monoflow.$EXTENSION"

                style = "position: absolute; left = -1000px;"
            }
            node.click()
            node.remove()
        }
    }

    fun openFile(onFileLoadedAction: (String) -> Unit) {
        document.body?.append {
            val fileInput = input(type = InputType.file) {
                attributes["accept"] = ".$EXTENSION"
                style = "position: absolute; left: -1000px;"
            }
            fileInput.onchange = {
                readFile(fileInput.files, onFileLoadedAction)
                fileInput.remove()
            }
            fileInput.click()
        }
    }

    private fun readFile(fileList: FileList?, onFileLoadedAction: (String) -> Unit) {
        val selectedFile = fileList?.get(0) ?: return
        val reader = FileReader()
        reader.onload = {
            val text = reader.result.toString()
            onFileLoadedAction(text)
        }
        reader.readAsText(selectedFile)
    }

    companion object {
        private const val EXTENSION = "mono"
    }
}

/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state

import kotlinx.browser.document
import mono.html.A
import mono.html.Input
import mono.html.InputType
import mono.html.setAttributes
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
        document.body?.run {
            val fileBlob = Blob(arrayOf(jsonString))
            val node = A(classes = "hidden") {
                href = URL.Companion.createObjectURL(fileBlob)
                setAttributes("download" to "$DEFAULT_FILENAME.$EXTENSION")
            }
            node.click()
            node.remove()
        }
    }

    fun openFile(onFileLoadedAction: (String) -> Unit) {
        document.body?.run {
            val fileInput = Input(InputType.FILE, classes = "hidden") {
                setAttributes("accept" to ".$EXTENSION")
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
        private const val DEFAULT_FILENAME = "monosketch"
        private const val EXTENSION = "mono"
    }
}

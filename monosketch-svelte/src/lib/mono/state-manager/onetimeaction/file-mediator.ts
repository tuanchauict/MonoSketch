/*
 * Copyright (c) 2024, tuanchauict
 */

/**
 * A mediator class for file interactions.
 */
export class FileMediator {
    saveFile(filename: string, jsonString: string) {
        const fileBlob = new Blob([jsonString]);
        const node = document.createElement('a');
        node.href = URL.createObjectURL(fileBlob);
        node.setAttribute('download', `${filename}.${FileMediator.EXTENSION}`);
        node.style.display = 'none';
        document.body.appendChild(node);
        node.click();
        document.body.removeChild(node);
    }

    openFile(onFileLoadedAction: (content: string) => void) {
        const fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.accept = `.${FileMediator.EXTENSION}`;
        fileInput.style.display = 'none';
        fileInput.onchange = () => {
            this.readFile(fileInput.files, onFileLoadedAction);
            document.body.removeChild(fileInput);
        };
        document.body.appendChild(fileInput);
        fileInput.click();
    }

    private readFile(fileList: FileList | null, onFileLoadedAction: (content: string) => void) {
        const selectedFile = fileList?.[0];
        if (!selectedFile) return;

        const reader = new FileReader();
        reader.onload = () => {
            const text = reader.result as string;
            onFileLoadedAction(text);
        };
        reader.readAsText(selectedFile);
    }

    private static readonly EXTENSION = 'mono';
}

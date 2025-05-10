import { Size } from "$libs/graphics-geo/size";
import { ChangeBound } from "$mono/shape/command/general-shape-commands";
import { ChangeText } from "$mono/shape/command/text-commands";
import type { Text } from "$mono/shape/shape/text";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import EditTextModalComponent from "$ui/modal/EditTextModal.svelte";

/**
 * A helper class to show edit text dialog for targeted text shape.
 */
export class EditTextShapeHelper {
    static showEditTextDialog(
        environment: CommandEnvironment,
        textShape: Text,
        isFreeText: boolean,
        onFinish: (text: string) => void = () => {
        },
    ) {
        const contentBound = textShape.contentBound;

        const dialog = new EditTextModal(textShape.text, (newText: string) => {
            environment.shapeManager.execute(new ChangeText(textShape, newText));
            if (!isFreeText) {
                this.adjustNormalTextSize(environment, textShape);
            }
        });

        dialog.setOnDismiss(() => {
            onFinish(textShape.text);
        });

        const contentWidth = isFreeText
            ? Math.max(environment.getWindowBound().width - contentBound.left, 30)
            : contentBound.width;
        const contentHeight = isFreeText ? 4 : contentBound.height;

        dialog.show(
            environment.toXPx(contentBound.left),
            environment.toYPx(contentBound.top),
            environment.toWidthPx(contentWidth),
            environment.toHeightPx(contentHeight),
        );
    }

    private static adjustNormalTextSize(environment: CommandEnvironment, text: Text) {
        const newBound = text.bound.copy({ size: this.getNormalTextActualSize(text) });
        environment.shapeManager.execute(new ChangeBound(text, newBound));
        environment.addSelectedShape(text);
    }

    private static getNormalTextActualSize(text: Text): Size {
        const height = text.extra.hasBorder()
            ? text.renderableText.getRenderableText().length + 2
            : text.renderableText.getRenderableText().length;
        const newHeight = height > text.bound.height ? height : text.bound.height;
        return Size.of(text.bound.width, newHeight);
    }
}

class EditTextModal {
    private component: EditTextModalComponent | null = null;

    private onDismiss: () => void = () => {
    };

    constructor(private initialText: string, private onTextChange: (newText: string) => void) {
        const modalContainer = document.querySelector("#edit-text-modal");
        if (!modalContainer) {
            throw new Error("Modal container not found");
        }
    }

    show(left: number, top: number, width: number, height: number) {
        const modalContainer = document.querySelector("#edit-text-modal");
        if (!modalContainer) {
            return;
        }

        this.component = new EditTextModalComponent(
            {
                target: modalContainer,
                props: {
                    initialText: this.initialText,
                    onTextChange: this.onTextChange,
                    onDismiss: () => {
                        this.dismiss();
                    },
                    left,
                    top,
                    width,
                    height,
                },
            },
        );
    }

    setOnDismiss(onDismiss: () => void) {
        this.onDismiss = onDismiss;
    }

    dismiss() {
        if (this.component) {
            this.component.$destroy();
            this.component = null;
            this.onDismiss();
        }
    }
}

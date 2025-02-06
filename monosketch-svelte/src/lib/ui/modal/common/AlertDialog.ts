/*
 * Copyright (c) 2025, tuanchauict
 */

import Dialog from "$ui/modal/common/Dialog.svelte";

export type ActionButtonModel = {
    text: string;
    onClick: () => void;
}

export type AlertDialogModel = {
    title?: string;
    message?: string;
    dismissOnClickingOutside?: boolean;
    primaryAction?: ActionButtonModel;
    secondaryAction?: ActionButtonModel;
    onDismiss?: () => void;
}

export function AlertDialog(props: AlertDialogModel) {
    const primaryAction = props.primaryAction ? {
        text: props.primaryAction.text,
        onClick: () => {
            props.primaryAction!.onClick();
            dialog.$destroy();
        },
    } : undefined;
    const secondaryAction = props.secondaryAction ? {
        text: props.secondaryAction.text,
        onClick: () => {
            props.secondaryAction!.onClick();
            dialog.$destroy();
        },
    } : undefined;

    const dialog = new Dialog({
        target: document.querySelector('#alert')!,
        props: {
            model: {
                ...props,
                primaryAction,
                secondaryAction,
                onDismiss: () => {
                    props.onDismiss?.();
                    dialog.$destroy();
                }
            },
        },
    });
}

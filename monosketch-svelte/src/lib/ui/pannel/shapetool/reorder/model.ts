import { ChangeOrderType } from "$mono/shape/command/shape-manager-commands";

export interface ButtonContentType {
    title: string;
    icon: string;
}

export const ChangeOrderTypes = [
    ChangeOrderType.FRONT,
    ChangeOrderType.FORWARD,
    ChangeOrderType.BACKWARD,
    ChangeOrderType.BACK,
];

export const ButtonContent: Record<ChangeOrderType, ButtonContentType> = {
    [ChangeOrderType.FRONT]: {
        title: 'Bring to Front',
        icon: 'M18,18h-9V15h-6V9h-3V0h9V3h6V9h3v9h0Zm-4-4V4H4V14h10Z',
    },
    [ChangeOrderType.FORWARD]: {
        title: 'Bring Forward',
        icon: 'M18,18h-12v-5h-6v-13h13v6h5v12h0Zm-17-6h11v-11h-11Z',
    },
    [ChangeOrderType.BACKWARD]: {
        title: 'Send Backward',
        icon: 'M6,18V13h-6V0h13V6h5V18Zm-5-6h5V6h6V1H1Z',
    },
    [ChangeOrderType.BACK]: {
        title: 'Send to Back',
        icon: 'M9,18V15h-6V9h-3V0h9V3h6V9h3v9Zm-5-4h5V9h-5Zm10-5V4h-5V9Z',
    },
};

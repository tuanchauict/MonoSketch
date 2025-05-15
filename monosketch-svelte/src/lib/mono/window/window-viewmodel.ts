import { Flow } from '$libs/flow';

export namespace WindowViewModel {
    export const windowSizeUpdateEventFlow = new Flow<boolean>(true);

    export const applicationActiveStateFlow = new Flow<boolean>(true);
}

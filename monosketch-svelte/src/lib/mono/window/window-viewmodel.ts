import { Flow } from '$libs/flow';

export namespace WindowViewModel {
    export const windowSizeUpdateEventFlow = new Flow<boolean>(true);
}

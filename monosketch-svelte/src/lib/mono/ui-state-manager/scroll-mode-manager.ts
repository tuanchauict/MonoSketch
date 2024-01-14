import { ScrollMode } from '$mono/ui-state-manager/states';
import { Flow } from 'lib/libs/flow';

export class ScrollModeManager {
    private _scrollModeFlow: Flow<ScrollMode> = new Flow<ScrollMode>(ScrollMode.BOTH);
    scrollModeFlow = this._scrollModeFlow.immutable();

    setScrollMode(scrollMode: ScrollMode): void {
        this._scrollModeFlow.value = scrollMode;
    }
}

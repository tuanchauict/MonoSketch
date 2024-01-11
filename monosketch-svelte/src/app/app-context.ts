import { LifecycleOwner } from '$mono/flow';
import { AppUiStateManager } from '$mono/ui-state-manager';

export class AppContext {
    appLifecycleOwner = new LifecycleOwner();

    appUiStateManager = new AppUiStateManager(this.appLifecycleOwner);

    onStart = (): void => {
        this.appLifecycleOwner.onStart();

        this.appUiStateManager.observeTheme(() => {
            console.log('Theme changed');
            // TODO: Update theme in the workspace
        });
    }
}

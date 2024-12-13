import { TODO } from "$libs/todo";
import { ShapeManager } from "$mono/shape/shape-manager";
import { WorkspaceDao } from "$mono/store-manager/dao/workspace-dao";
import { BrowserManager } from "$mono/window/browser-manager";
import { LifecycleOwner } from 'lib/libs/flow';
import { AppUiStateManager } from '$mono/ui-state-manager';

/**
 * Main class of the app to handle all kinds of events, UI, actions, etc.
 */
export class AppContext {
    appLifecycleOwner = new LifecycleOwner();

    appUiStateManager = new AppUiStateManager(this.appLifecycleOwner);

    shapeManager = new ShapeManager();

    onStart = (): void => {
        this.appLifecycleOwner.onStart();

        this.init();

        this.appUiStateManager.observeTheme(() => {
            console.log('Theme changed');
            // TODO: Update theme in the workspace
        });
    };

    private init() {
        const browserManager = new BrowserManager((url: string) => {
            console.log('URL updated:', url);
            // TODO: Update the workspace based on the URL
            TODO("Update the workspace based on the URL");
        });
        browserManager.startObserveStateChange(
            this.shapeManager.rootIdFlow,
            this.appLifecycleOwner,
            WorkspaceDao.instance);

        // TODO: Replicate from MonoSketchApplication
    }
}

import { ActionManager } from "$mono/action-manager/action-manager";
import { OneTimeAction } from "$mono/action-manager/one-time-actions";
import { ShapeManager } from "$mono/shape/shape-manager";
import { WorkspaceDao } from "$mono/store-manager/dao/workspace-dao";
import { BrowserManager } from "$mono/window/browser-manager";
import { LifecycleOwner } from 'lib/libs/flow';
import { AppUiStateManager } from '$mono/ui-state-manager/app-ui-state-manager';

/**
 * Main class of the app to handle all kinds of events, UI, actions, etc.
 */
export class AppContext {
    appLifecycleOwner = new LifecycleOwner();

    appUiStateManager = new AppUiStateManager(this.appLifecycleOwner);

    shapeManager = new ShapeManager();

    actionManager = new ActionManager(this.appLifecycleOwner);

    onStart = (): void => {
        this.appLifecycleOwner.onStart();

        this.init();

        this.appUiStateManager.observeTheme();
        this.actionManager.observeKeyCommand(
            this.appUiStateManager.keyCommandFlow.map((keyCommand) => keyCommand.command),
        )
    };

    private init() {
        this.actionManager.installDebugCommand();

        const browserManager = new BrowserManager((projectId: string) => {
            this.actionManager.setOneTimeAction(OneTimeAction.ProjectAction.SwitchProject(projectId));
        });
        browserManager.startObserveStateChange(
            this.shapeManager.rootIdFlow,
            this.appLifecycleOwner,
            WorkspaceDao.instance);

        // TODO: Replicate from MonoSketchApplication
    }
}

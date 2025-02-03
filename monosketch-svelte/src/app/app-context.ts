import type { Workspace } from "$app/workspace";
import { ActionManager } from "$mono/action-manager/action-manager";
import { OneTimeAction } from "$mono/action-manager/one-time-actions";
import { DEBUG_MODE } from "$mono/build_environment";
import { MonoBoard } from "$mono/monobitmap/board";
import { MonoBitmapManager } from "$mono/monobitmap/manager/mono-bitmap-manager";
import { SelectedShapeManager } from "$mono/shape/selected-shape-manager";
import { ShapeClipboardManager } from "$mono/shape/shape-clipboard-manager";
import { ShapeManager } from "$mono/shape/shape-manager";
import { MainStateManager } from "$mono/state-manager/main-state-manager";
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

    monoBoard: MonoBoard = new MonoBoard();
    shapeManager = new ShapeManager();

    actionManager = new ActionManager(this.appLifecycleOwner);

    browserManager = new BrowserManager((projectId: string) => {
        this.actionManager.setOneTimeAction(OneTimeAction.ProjectAction.SwitchProject(projectId));
    });

    workspaceDao: WorkspaceDao = WorkspaceDao.instance;

    // Workspace must be set before MainStateManager is initiated.
    private workspace: Workspace | null = null;
    private mainStateManager: MainStateManager | null = null;

    onStart = (): void => {
        this.appLifecycleOwner.onStart();

        this.init();

        this.appUiStateManager.observeTheme();
        this.actionManager.observeKeyCommand(
            this.appUiStateManager.keyCommandFlow.map((keyCommand) => keyCommand.command),
        );

        this.mainStateManager?.onStart(this.appLifecycleOwner);
        this.browserManager.startObserveStateChange(this.shapeManager.rootIdFlow, this.appLifecycleOwner,);
    };

    setWorkspace(workspace: Workspace) {
        this.workspace = workspace;
        this.init();

        if (this.appLifecycleOwner.isActive) {
            this.mainStateManager?.onStart(this.appLifecycleOwner);
        }
    }

    private init() {
        if (this.workspace === null) {
            if (DEBUG_MODE) {
                console.warn('Workspace is not set');
            }
            return;
        }
        if (this.mainStateManager !== null) {
            if (DEBUG_MODE) {
                console.warn('MainStateManager is already set');
            }
            return;
        }
        this.actionManager.installDebugCommand();

        this.mainStateManager = new MainStateManager(
            this.monoBoard,
            this.shapeManager,
            new SelectedShapeManager(),
            new MonoBitmapManager(),
            this.workspace,
            this.workspaceDao,
            this.actionManager,
            new ShapeClipboardManager(),
            this.appUiStateManager,
            this.browserManager.rootIdFromUrl,
        );
        // TODO: Replicate from MonoSketchApplication
    }
}

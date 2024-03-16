import { Flow, LifecycleOwner } from "$libs/flow";
import type { WorkspaceDao } from "$mono/store-manager/dao/workspace-dao";

/**
 * A class for managing the states related to the browser such as the title, address bar, etc.
 */
export class BrowserManager {
    private readonly onUrlUpdate: (url: string) => void;
    private willChangedByUrlPopStateEvent: boolean = false;

    constructor(onUrlUpdate: (url: string) => void) {
        this.onUrlUpdate = onUrlUpdate;

        // Initialize the class by calling the URL update callback with the current root ID
        this.onUrlUpdate(this.rootIdFromUrl);

        // Listen to popstate events to handle browser navigation (back/forward)
        window.onpopstate = (_event: PopStateEvent) => {
            this.willChangedByUrlPopStateEvent = true;
            this.onUrlUpdate(this.rootIdFromUrl);
        };
    }

    // Getter to retrieve the URL search parameters
    private get urlSearchParams(): URLSearchParams {
        return new URLSearchParams(window.location.search);
    }

    // Getter to retrieve the root ID from the URL
    get rootIdFromUrl(): string {
        // Replace space char with + since '+' will be converted to ' ' when reading value from
        // URLSearchParams. The space character is not allowed in the URL.
        return this.urlSearchParams.get(BrowserManager.URL_PARAM_ID) || '';
    }

    // Method to start observing state changes
    startObserveStateChange(
        workingProjectIdFlow: Flow<string>,
        lifecycleOwner: LifecycleOwner,
        workspaceDao: WorkspaceDao,
    ): void {
        // Observe changes to the project ID and update the document title
        workingProjectIdFlow.observe(lifecycleOwner, projectId => {
            const project = workspaceDao.getObject(projectId);
            document.title = `${project.name} - MonoSketch`;
        });

        // Observe distinct changes to the project ID and update the URL
        workingProjectIdFlow.distinctUntilChanged().observe(lifecycleOwner, projectId => {
            if (projectId === this.rootIdFromUrl || this.willChangedByUrlPopStateEvent) {
                this.willChangedByUrlPopStateEvent = false;
                return;
            }
            const searchParams = this.urlSearchParams;
            searchParams.set(BrowserManager.URL_PARAM_ID, projectId);
            const newUrl = `${window.location.origin}${window.location.pathname}?${searchParams}`;
            window.history.pushState({ path: newUrl }, "", newUrl);
        });
    }

    // Static property for the URL parameter key
    private static URL_PARAM_ID: string = 'id';

    // Static method to open a project in a new tab
    static openInNewTab(projectId: string): void {
        window.open(`?${BrowserManager.URL_PARAM_ID}=${projectId}`, '_blank');
    }
}

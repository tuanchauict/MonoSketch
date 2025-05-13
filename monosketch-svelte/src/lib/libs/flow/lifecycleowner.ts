export interface LifecycleObserver {
    onStart?(): void;

    onStop?(): void;
}

export enum LifecycleState {
    INITIALIZED,
    STARTED,
    STOPPED,
}

export class LifecycleOwner {
    private observers: LifecycleObserver[] = [];
    private state: LifecycleState = LifecycleState.INITIALIZED;

    static start(): LifecycleOwner {
        const owner = new LifecycleOwner();
        owner.onStart();
        return owner;
    }

    get isActive(): boolean {
        return this.state === LifecycleState.STARTED;
    }

    addObserver(observer: LifecycleObserver) {
        if (this.state === LifecycleState.STOPPED) {
            return;
        }

        this.observers.push(observer);
        if (this.state === LifecycleState.STARTED) {
            observer.onStart?.();
        }
    }

    /**
     * Called when the LifecycleOwner is ready to start.
     * Do not override this method, override onStartInternal instead.
     */
    onStart() {
        if (this.state === LifecycleState.STARTED) {
            return;
        }

        this.state = LifecycleState.STARTED;
        for (const observer of this.observers) {
            observer.onStart?.();
        }
        this.onStartInternal();
    }

    protected onStartInternal() {}

    /**
     * Called when the LifecycleOwner is ready to stop.
     * Do not override this method, override onStopInternal instead.
     */
    onStop() {
        if (this.state === LifecycleState.STOPPED) {
            return;
        }

        this.state = LifecycleState.STOPPED;
        this.onStopInternal();
        for (const observer of this.observers) {
            observer.onStop?.();
        }
    }

    protected onStopInternal() {}
}

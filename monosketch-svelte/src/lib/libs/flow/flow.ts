import { type LifecycleObserver, LifecycleOwner } from './lifecycleowner';
import { type Comparable } from '$libs/comparable';

interface Observer<T> {
    onChange(value: T): void;
}

class SimpleObserver<T> implements Observer<T> {
    constructor(private callback: (value: T) => void) {}

    onChange(value: T): void {
        if (value !== undefined) {
            this.callback(value);
        }
    }
}

class ThrottleObserver<T> implements Observer<T> {
    private timeoutId: number | undefined;

    private currentValue: T | undefined;

    constructor(
        private observer: Observer<T>,
        private timeout: number,
    ) {
        if (timeout < 0) {
            throw new Error('Timeout must be >= 0');
        }
    }

    onChange = (value: T) => {
        this.currentValue = value;
        if (this.timeoutId !== undefined) {
            return;
        }
        if (this.timeout === 0) {
            this.timeoutId = requestAnimationFrame(this.timeoutTick);
        } else {
            this.timeoutId = setTimeout(this.timeoutTick, this.timeout);
        }
    };

    private timeoutTick = () => {
        const newValue = this.currentValue;
        if (newValue === undefined) {
            return;
        }
        this.observer.onChange(newValue);
        this.timeoutId = undefined;
    };
}

export class Flow<T> {
    private valueInternal: T | undefined = undefined;
    private observers: Observer<T>[] = [];
    private internalObservers: Map<Flow<unknown>, Observer<T>> = new Map();

    private isImmutable = false;
    private parent?: Array<Flow<unknown>>;
    private transform?: (a: Array<unknown>) => T;

    /**
     * A flag that indicates whether the value of this flow should be updated when the parent flow's value changes
     * regardless of whether this flow has subscribers.
     * Turning this flag on will cause the value of this flow to be updated even if there are no subscribers.
     * Only use this flag when reading the current value of the flow is required rather than observing the flow.
     */
    private isValueUpdatedReactivelyRequired = false;

    private static immutable<T0, T>(
        parent: Array<Flow<unknown>>,
        transform: (value: T0) => T,
    ): Flow<T> {
        const flow = new Flow<T>();
        flow.parent = parent;
        // @ts-expect-error : Allow unsafe call to transform.
        flow.transform = transform;
        flow.isImmutable = true;
        return flow;
    }

    static combine2<T0, T1, R>(
        flow0: Flow<T0>,
        flow1: Flow<T1>,
        transform: (value0: T0, value1: T1) => R,
    ): Flow<R> {
        return Flow.combineList([flow0, flow1], (array) => {
            return transform(array[0] as T0, array[1] as T1);
        });
    }

    static combine3<T0, T1, T2, R>(
        flow0: Flow<T0>,
        flow1: Flow<T1>,
        flow2: Flow<T2>,
        transform: (value0: T0, value1: T1, value2: T2) => R,
    ): Flow<R> {
        return Flow.combineList([flow0, flow1, flow2], (array) => {
            return transform(array[0] as T0, array[1] as T1, array[2] as T2);
        });
    }

    static combine4<T0, T1, T2, T3, R>(
        flow0: Flow<T0>,
        flow1: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        transform: (value0: T0, value1: T1, value2: T2, value3: T3) => R,
    ): Flow<R> {
        return Flow.combineList([flow0, flow1, flow2, flow3], (array) => {
            return transform(array[0] as T0, array[1] as T1, array[2] as T2, array[3] as T3);
        });
    }

    static combineList<R>(
        flows: Array<Flow<unknown>>,
        transform: (values: Array<unknown>) => R,
    ): Flow<R> {
        if (flows.length === 0) {
            throw new Error('flows must not be empty');
        }
        if (flows.length === 1) {
            console.warn('You are combining a single flow. Use flow.map instead.');
            // @ts-expect-error : Allow unsafe call to transform.
            return flows[0].map(transform);
        }

        const flow = Flow.immutable(flows, transform);
        for (const parent of flows) {
            parent.addInternalObserver(
                flow,
                new SimpleObserver(() => {
                    // TODO: Getting value with `.value` can increase the computation cost since the value is computed
                    //  from its parent flows. We should consider optimizing this.
                    const values = flows.map((flow) => flow.value);
                    if (values.includes(undefined)) {
                        // Only update the value when all values are available.
                        return;
                    }
                    flow.setValueInternal(transform(values));
                }),
            );
        }
        return flow;
    }

    // @ts-expect-error : Allow the value to be undefined.
    constructor(value: T = undefined) {
        if (value !== undefined) {
            this.valueInternal = value;
        }
    }

    set value(value: T) {
        if (this.isImmutable) {
            throw new Error('Flow is immutable');
        }
        if (value === undefined) {
            throw new Error('Value cannot be undefined');
        }

        this.setValueInternal(value);
    }

    get value(): T | undefined {
        if (this.valueInternal !== undefined) {
            return this.valueInternal;
        }
        const args = this.parent?.map((flow) => flow.value);
        if (args === undefined || args.includes(undefined)) {
            return undefined;
        }
        return this.transform!(args);
    }

    private setValueInternal(value: T | undefined) {
        if (value === undefined) {
            return;
        }

        this.valueInternal = value;

        for (const observer of this.observers) {
            this.delegateValueToObserver(observer, value);
        }

        for (const [flow, observer] of this.internalObservers) {
            if (flow.hasSubscribers()) {
                this.delegateValueToObserver(observer, value);
            }
        }
    }

    /**
     * Makes the value of this flow updated when the parent flow's value changes regardless of whether this flow has
     * subscribers.
     * This method is useful when reading the current value of the flow is required rather than observing the flow.
     *
     * Note that this method also makes the parents of this flow updated reactively.
     */
    makeValueUpdateReactively() {
        this.isValueUpdatedReactivelyRequired = true;
        // Update the value when this flag is turned on.
        // This does nothing if the value is already up-to-date, so it's safe to call this method multiple times.
        // Otherwise, this method will update the value to the latest value of the parent flow.
        this.valueInternal = this.value;
    }

    map<R>(transform: (value: T) => R): Flow<R> {
        const flow = Flow.immutable([this], (args: Array<T>) => transform(args[0]));
        this.addInternalObserver(
            flow,
            new SimpleObserver((value) => {
                flow.setValueInternal(transform(value));
            }),
        );
        return flow;
    }

    /**
     * Returns a flow that only emits a value when the current value is different from the previous value.
     *
     * Note: the first value set to the parent flow will always be emitted even if it is the same as the previous value.
     * This is a limitation of the current implementation.
     */
    distinctUntilChanged(): Flow<T> {
        const flow = Flow.immutable([this], (args: Array<T>) => args[0]);
        this.addInternalObserver(
            flow,
            new SimpleObserver((value) => {
                if (value === undefined) {
                    // Ignore undefined values.
                    return;
                }

                if (equals(value, flow.valueInternal)) {
                    return;
                }
                flow.setValueInternal(value);
            }),
        );
        return flow;
    }

    throttle(timeout: number): Flow<T> {
        if (timeout < 0) {
            return this;
        }
        const flow = Flow.immutable([this], (args: Array<T>) => args[0]);
        this.addInternalObserver(
            flow,
            new ThrottleObserver(
                new SimpleObserver((value) => {
                    flow.setValueInternal(value);
                }),
                timeout,
            ),
        );
        return flow;
    }

    /**
     * Returns an immutable version of this flow.
     */
    immutable(): Flow<T> {
        return this.map((value: T) => value);
    }

    combine<T1, R>(another: Flow<T1>, transform: (value0: T, value1: T1) => R): Flow<R> {
        return Flow.combine2(this, another, transform);
    }

    /**
     * Observes this flow with active lifecycle owner.
     *
     * The observer will be called immediately with the current defined value of the flow (do nothing if the current
     * value is undefined).
     * @param lifecycleOwner The lifecycle owner that the observer will be attached to. The observer will be removed
     * when the lifecycle owner is stopped. If the lifecycle owner is inactive, the observer will not be added.
     * @param observer
     */
    observe(lifecycleOwner: LifecycleOwner, observer: (value: T) => void) {
        if (!lifecycleOwner.isActive) {
            return;
        }
        const simpleObserver = new SimpleObserver(observer);
        this.observers.push(simpleObserver);
        lifecycleOwner.addObserver(
            new OnStopLifecycleObserver(() => {
                const index = this.observers.indexOf(simpleObserver);
                if (index !== -1) {
                    this.observers.splice(index, 1);
                }
                if (this.isImmutable && !this.hasSubscribers()) {
                    // When there are no more subscribers, we can clear the value since the parent flow will not propagate
                    // its state to this flow anymore.
                    this.valueInternal = undefined;
                }
            }),
        );

        this.delegateValueToObserver(simpleObserver, this.value);
    }

    private addInternalObserver(key: Flow<unknown>, observer: Observer<T>) {
        this.internalObservers.set(key, observer);
    }

    private delegateValueToObserver(observer: Observer<T>, value: T | undefined) {
        if (value !== undefined) {
            observer.onChange(value);
        }
    }

    private hasSubscribers(): boolean {
        if (this.isValueUpdatedReactivelyRequired || this.observers.length > 0) {
            return true;
        }
        for (const child of this.internalObservers.keys()) {
            if (child.hasSubscribers()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Stops receiving updates from the parent flow.
     * This method is useful when you want to stop receiving updates from the parent flow once this flow is no longer
     * used.
     */
    stopReceivingUpdates() {
        for (const parent of this.parent!) {
            parent.internalObservers.delete(this);
        }
    }
}

class OnStopLifecycleObserver implements LifecycleObserver {
    constructor(private callback: () => void) {}

    onStop() {
        this.callback();
    }
}

/**
 * Compares two values for equality.
 * If both values are Comparable, then the equals method of the first value will be used.
 */
function equals(a: unknown, b: unknown): boolean {
    if (a === b) {
        // This also covers the case when a and b are both undefined or null.
        // And also make use of the default equality check for primitive types.
        return true;
    }

    if (a === null || a === undefined) {
        // Since a and b are not equal, b must not be null or undefined.
        return false;
    }

    if ((a as Comparable).equals) {
        return (a as Comparable).equals(b);
    }

    return false;
}

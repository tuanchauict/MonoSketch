<script lang="ts">
    import MainDropDown from "./menu/main-dropdown/MainDropDown.svelte";
    import {onDestroy, onMount} from "svelte";
    import {modalViewModel} from "./viewmodel";
    import {LifecycleOwner} from "../../mono/flow";

    let mainDropDownTarget: (HTMLElement | null) = null;

    const lifecycleOwner = new LifecycleOwner();
    onMount(() => {
        lifecycleOwner.onStart();

        modalViewModel.mainDropDownMenuTargetFlow.observe(lifecycleOwner, (target) => {
            mainDropDownTarget = target;
        });
    });

    onDestroy(() => {
        lifecycleOwner.onStop();
    });
</script>
{#if mainDropDownTarget}
    <MainDropDown target={mainDropDownTarget}/>
{/if}
```

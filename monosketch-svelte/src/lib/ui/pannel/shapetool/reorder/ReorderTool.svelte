<script lang="ts">
    import Section from "../../common/Section.svelte";
    import Icon from "./Icon.svelte";
    import {ReorderType} from "./model";

    function onClick(icon: ReorderType) {
        console.log(icon);
    }
</script>

<Section hasBorderTop={false}>
    <div>
        <Icon icon={ReorderType.FRONT} {onClick}/>
        <Icon icon={ReorderType.UPWARD} {onClick}/>
        <Icon icon={ReorderType.BACKWARD} {onClick}/>
        <Icon icon={ReorderType.BACK} {onClick}/>
    </div>
</Section>

<style>
    div {
        display: flex;
        flex-direction: row;
        justify-content: space-around;
        align-items: center;
    }
</style>

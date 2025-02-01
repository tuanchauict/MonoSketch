<!--
  - Copyright (c) 2025, tuanchauict
  -->

<script lang="ts">
    export let parent: string = '#modal';

    function portal(node: HTMLElement) {
        document.querySelector(parent)!!.appendChild(node);

        return {
            destroy() {
                node.remove();
            },
        };
    }
</script>

<div use:portal class="portal">
    <slot/>
</div>

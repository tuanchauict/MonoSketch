<!--
  - Copyright (c) 2025, tuanchauict
  -->

<script lang="ts">
    export let parent: HTMLElement = document.querySelector('#modal')!!;

    function portal(node: HTMLElement) {
        parent.appendChild(node);

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

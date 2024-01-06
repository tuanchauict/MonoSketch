<script>
export let moreSpaceLeft = false;
export let moreSpaceRight = false;
</script>

<div class:add-space-left="{moreSpaceLeft}" class:add-space-right="{moreSpaceRight}">
    <slot />
</div>

<style>
div {
    display: flex;
    flex-direction: row;
    height: 100%;
    align-items: center;
}

.add-space-left {
    margin-left: 4px;
}

.add-space-right {
    margin-right: 4px;
}
</style>

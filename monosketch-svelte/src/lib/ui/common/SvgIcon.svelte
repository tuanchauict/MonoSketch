<script lang="ts">
export let size: number | number[];
export let viewBoxSize: number | number[] = [];
export let fillColor: string = 'currentColor';

let viewBoxSizeValue = viewBoxSize ? viewBoxSize : size;

let width = size instanceof Array ? size[0] : size;
let height = size instanceof Array ? size[1] : size;
let viewBoxWidth = viewBoxSizeValue instanceof Array ? viewBoxSizeValue[0] : viewBoxSizeValue;
let viewBoxHeight = viewBoxSizeValue instanceof Array ? viewBoxSizeValue[1] : viewBoxSizeValue;
</script>

<svg {width} {height} viewBox="0 0 {viewBoxWidth} {viewBoxHeight}" fill="{fillColor}">
    <slot />
</svg>

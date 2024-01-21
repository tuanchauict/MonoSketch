<script lang="ts">
import NoBackgroundModal from '../../common/NoBackgroundModal.svelte';
import type { Rect } from '$libs/graphics-geo/rect';

export let targetBounds: Rect;
export let width: number = 180;
export let onDismiss: () => void;
export let horizontalOffsetPx: number = 0;
export let verticalOffsetPx: number = 0;

$: left = targetBounds.left + horizontalOffsetPx;
$: top = targetBounds.top + targetBounds.height + verticalOffsetPx;
</script>

<NoBackgroundModal {onDismiss} {left} {top} {width}>
    <div class="container">
        <slot />
    </div>
</NoBackgroundModal>

<style lang="scss">
@import '../../../../style/variables.scss';

.container {
    border-radius: 6px;
    background: var(--modal-container-bg);
    border: 1px solid var(--mainmenu-divider-color);
    padding: 6px 0;
    font-family: $monospaceFont;
    width: 100%;
    margin-right: 4px;
}
</style>

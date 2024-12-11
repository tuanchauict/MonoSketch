<script lang="ts">
import KeyboardShortcutContent from './KeyboardShortcutContent.svelte';
import SvgIcon from '../../common/SvgIcon.svelte';
import { modalViewModel } from '../viewmodel';

function dismiss() {
    modalViewModel.keyboardShortcutVisibilityStateFlow.value = false;
}
</script>

<div class="modal">
    <div class="modal-content">
        <h3>Keyboard Shortcuts</h3>

        <KeyboardShortcutContent />
    </div>

    <button on:click="{dismiss}">
        <SvgIcon size="{16}">
            <path
                d="M13.854 2.146a.5.5 0 0 1 0 .708l-11 11a.5.5 0 0 1-.708-.708l11-11a.5.5 0 0 1 .708 0Z"
            ></path>
            <path
                d="M2.146 2.146a.5.5 0 0 0 0 .708l11 11a.5.5 0 0 0 .708-.708l-11-11a.5.5 0 0 0-.708 0Z"
            ></path>
        </SvgIcon>
    </button>
</div>

<style lang="scss">
@import '../../../style/variables.scss';

.modal {
    background: var(--modal-container-bg);
    color: var(--modal-content-color);

    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
}

.modal-content {
    background: var(--modal-content-bg);
    color: var(--modal-content-color);

    display: flex;
    flex-direction: column;
    width: 100%;
    height: 100%;
}

h3 {
    margin: 16px 0 20px;
    text-align: center;
    font-weight: 300;
    font-size: 18px;
    font-family: $monospaceFont;
    color: var(--modal-title-color);
}

button {
    position: absolute;
    right: 0;
    top: 0;
    border: 0;
    background: transparent;
    padding: 12px;
    cursor: pointer;

    color: inherit;
}
</style>

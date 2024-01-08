<script lang="ts">
import SvgIcon from '../../common/SvgIcon.svelte';
import { modalViewModel } from '../../modal/viewmodel';
import { TargetBounds } from '../../modal/model';

export let filename: string;

function showDropDownMenu(e: MouseEvent) {
    modalViewModel.currentFileDropDownMenuTargetFlow.value = TargetBounds.fromElement(
        e.currentTarget as HTMLElement,
    );
}
</script>

<div class="container">
    <div class="file-info-container" on:click="{showDropDownMenu}">
        <span class="file-info">
            {filename}
        </span>
        <div class="icon">
            <SvgIcon size="{16}">
                <path
                    d="M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z"
                ></path>
            </SvgIcon>
        </div>
    </div>
</div>

<style lang="scss">
@import '../../../style/variables.scss';

$max-text-width: 210px;
.container {
    display: flex;
    align-items: center;
    height: 100%;
    width: $max-text-width + 31px;
}

.file-info-container {
    display: flex;
    align-items: center;

    border-radius: 6px;
    padding: 8px 4px 8px 8px;
    font-family: $monospaceFont;
    cursor: pointer;

    &:hover {
        background: var(--nav-action-hover-bg);
    }
}

.file-info {
    display: flex;
    align-items: center;

    font-size: 14px;
    max-width: $max-text-width;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
}

.icon {
    display: flex;
    margin-left: 4px;
    padding-top: 4px;
}
</style>

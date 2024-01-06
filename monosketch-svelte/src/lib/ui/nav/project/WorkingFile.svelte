<script lang="ts">
export let filename: string;
</script>

<div class="container">
    <div class="file-info-container">
        <span class="file-info">
            {filename}
        </span>
        <svg class="icon" width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path
                d="M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z"
            ></path>
        </svg>
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

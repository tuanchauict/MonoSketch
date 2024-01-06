<div>
    <h2>
        <a href="https://monosketch.io" target="_blank">MonoSketch</a>
    </h2>
</div>

<style lang="scss">
@import '../../../style/variables.scss';

$content-color: var(--header-color);

div {
    height: 100%;
    display: flex;
    padding-left: 16px;
}

h2 {
    margin: 0;
    height: 100%;
    display: flex;
    flex-direction: row;
    align-items: center;

    a {
        font-size: 22px;
        letter-spacing: 0.2px;
        font-family: $monospaceFont;
        font-weight: 400;
        line-height: 18px;
        text-decoration: none;
        color: $content-color;

        &:hover,
        &:active {
            color: $content-color;
        }
    }
}
</style>

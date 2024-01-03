<script lang="ts">
    import {ScrollMode} from './model';
    import AppIcon from "../common/AppIcon.svelte";
    import TooltipTarget from "../../modal/menu/tooltip/TooltipTarget.svelte";
    import {Direction} from "../../modal/menu/tooltip/model";

    export let scrollMode: ScrollMode = ScrollMode.BOTH;

    $: path = getPath(scrollMode);

    function getPath(mode: ScrollMode): string {
        switch (mode) {
            case ScrollMode.BOTH:
                return 'M25 20a5 5 0 1 1-10 0 5 5 0 0 1 10 0ZM19.01.582a1.134 1.134 0 0 1 1.98 0l4.87 8.834c.41.744-.133 1.651-.99 1.651h-9.74c-.857 0-1.4-.907-.99-1.651L19.01.582ZM20.99 39.418a1.134 1.134 0 0 1-1.98 0l-4.87-8.834c-.41-.744.133-1.651.99-1.651h9.74c.857 0 1.4.907.99 1.651l-4.87 8.834ZM.582 20.99a1.134 1.134 0 0 1 0-1.98l8.834-4.87c.744-.41 1.651.133 1.651.99v9.74c0 .857-.907 1.4-1.651.99L.582 20.99ZM39.418 19.01a1.134 1.134 0 0 1 0 1.98l-8.834 4.87c-.744.41-1.651-.133-1.651-.99v-9.74c0-.857.907-1.4 1.651-.99l8.834 4.87Z';
            case ScrollMode.VERTICAL:
                return 'M25 20a5 5 0 1 1-10 0 5 5 0 0 1 10 0ZM19.01.582a1.134 1.134 0 0 1 1.98 0l4.87 8.834c.41.744-.133 1.651-.99 1.651h-9.74c-.857 0-1.4-.907-.99-1.651L19.01.582ZM20.99 39.418a1.134 1.134 0 0 1-1.98 0l-4.87-8.834c-.41-.744.133-1.651.99-1.651h9.74c.857 0 1.4.907.99 1.651l-4.87 8.834Z';
            case ScrollMode.HORIZONTAL:
                return 'M25 20a5 5 0 1 1-10 0 5 5 0 0 1 10 0ZM.582 20.99a1.134 1.134 0 0 1 0-1.98l8.834-4.87c.744-.41 1.651.133 1.651.99v9.74c0 .857-.907 1.4-1.651.99L.582 20.99ZM39.418 19.01a1.134 1.134 0 0 1 0 1.98l-8.834 4.87c-.744.41-1.651-.133-1.651-.99v-9.74c0-.857.907-1.4 1.651-.99l8.834 4.87Z';
        }
    }

    function changeMode() {
        console.log('click');
        switch (scrollMode) {
            case ScrollMode.BOTH:
                scrollMode = ScrollMode.VERTICAL;
                break;
            case ScrollMode.VERTICAL:
                scrollMode = ScrollMode.HORIZONTAL;
                break;
            case ScrollMode.HORIZONTAL:
                scrollMode = ScrollMode.BOTH;
                break;
        }
    }
</script>

<TooltipTarget direction={Direction.BOTTOM} text="Scroll mode" offsetVertical={6.5}>
    <AppIcon size={16} viewBoxSize={40} onClick={changeMode}>
        <path d="{path}"/>
    </AppIcon>
</TooltipTarget>

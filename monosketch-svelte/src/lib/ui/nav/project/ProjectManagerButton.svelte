<script lang="ts">
import AppIcon from '../common/AppIcon.svelte';
import { Direction } from '../../modal/tooltip/model';
import TooltipTarget from '../../modal/tooltip/TooltipTarget.svelte';
import { modalViewModel } from '../../modal/viewmodel';

function onClick() {
    modalViewModel.projectManagementVisibilityStateFlow.value = true;
}
</script>

<TooltipTarget direction="{Direction.BOTTOM}" text="Manage projects" offsetVertical="{6.5}">
    <AppIcon viewBoxSize="{800}" size="{16}" fill="none" {onClick}>
        <path
            d="M771 442 644 145c-7-17-24-28-43-28H199c-19 0-36 11-43 28L29 442m742 0v186c0 51-42 93-93 93H122c-51 0-93-42-93-93V442m742 0H586c-26 0-47 21-47 47v46c0 26-21 47-46 47H307c-25 0-46-21-46-47v-46c0-26-21-47-46-47H29m242-208 244-2m-291 94h337"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="50"
        ></path>
    </AppIcon>
</TooltipTarget>

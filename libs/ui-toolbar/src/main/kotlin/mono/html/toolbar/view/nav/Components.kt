/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.nav

import androidx.compose.runtime.Composable
import mono.scopedcss.css
import mono.ui.compose.ext.classes
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

private val toolbarContainer = css(
    """
    display: flex;
    flex-direction: row;
    height: 100%;
    align-items: center;

    &.add-space-left {
        margin-left: 4px;
    }

    &.add-space-right {
        margin-right: 4px;
    }

    .app-icon-container {
        display: flex;
        align-items: center;
        height: 100%;
    }

    .app-icon {
        display: flex;
        justify-content: center;
        align-items: center;
        border-radius: 50%;
        width: 34px;
        height: 34px;
        cursor: pointer;

        &:hover {
            background: var(--nav-action-hover-bg);
        }
    }

    .theme-dark-mode {
        :root.dark & {
            display: none;
        }
    }

    .theme-light-mode {
        :root.light & {
            display: none;
        }
    }
    """
)

@Composable
internal fun ToolbarContainer(
    addSpaceLeft: Boolean = false,
    addSpaceRight: Boolean = false,
    content: ContentBuilder<HTMLDivElement>? = null
) {
    Div(
        attrs = {
            classes(
                toolbarContainer,
                "add-space-left" to addSpaceLeft,
                "add-space-right" to addSpaceRight
            )
        }
    ) {
        content?.invoke(this)
    }
}

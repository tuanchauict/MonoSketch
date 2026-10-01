/*
 * Copyright (c) 2026, tuanchauict
 */

package mono.html.toolbar.view.nav

import mono.scopedcss.css

/**
 * The class name for the container of an [appIcon] which needs to fill the toolbar's height.
 */
internal val appIconContainer = css(
    """
    display: flex;
    align-items: center;
    height: 100%;
    """
)

/**
 * The class name for a round icon button on the toolbar.
 */
internal val appIcon = css(
    """
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
    """
)

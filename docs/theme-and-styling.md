# Theme and styling

This document describes the light and dark themes, the canvas colors and the CSS of the HTML parts.

## Theme modes

MonoSketch has two theme modes: `LIGHT` and `DARK` (`ThemeMode` in the `ui-theme` module). The default is `DARK`.

| Class | Module | Responsibility |
|---|---|---|
| `ThemeManager` | `ui-theme` | Keeps the current mode in `themeModeLiveData`. Gives the canvas colors. |
| `AppThemeManager` | `ui-app-state-manager` | Reads and saves the mode. Sets the CSS class of the page. |

`ThemeManager` is a singleton. Call `ThemeManager.getInstance()` to get it.

### Change the theme

1. The theme icon in the navigation bar calls `ThemeManager.setTheme(mode)`.
2. `AppThemeManager` observes `themeModeLiveData`. It sets the class of the `<html>` element to `light` or `dark`.
3. It calls `forceUiUpdate`. `MainStateManager` then redraws all canvases with the new colors.
4. It saves the mode in `settings/theme-mode`.

At startup, `AppThemeManager` reads `settings/theme-mode`. If the key is missing, it uses `DARK`.

`AppThemeManager` also observes the `settings/theme-mode` key in storage. When a different tab changes the theme, this tab changes it too.

**Note:** step 2 replaces all classes of `<html>`. `index.html` sets `class="dark w-full h-full"`, so `w-full` and `h-full` are removed.

## Canvas colors

The canvases do not use CSS. They get the colors from the `ThemeColor` enum in the `ui-theme` module. Call `ThemeManager.getColorCode(color)` to get the color for the current mode.

| ThemeColor | Light | Dark |
|---|---|---|
| `AxisBackground` | `#EEEEEE` | `#1E1E1E` |
| `AxisText` | `#666666` | `#666666` |
| `AxisRule` | `#444444` | `#666666` |
| `GridBackground` | `#FFFFFF` | `#121212` |
| `GridLine` | `#d9d9d9` | `#282828` |
| `GridLineZero` | `#BBBBBB` | `#323232` |
| `Shape` | `#000000` | `#F0F0F0` |
| `ShapeSelected` | `#0D7CFF` | `#FFD82F` |
| `ShapeTextEditing` | `#0767C6` | `#956d04` |
| `ShapeLineConnectTarget` | `#E86A33` | `#00FFF6` |
| `SelectionAreaStroke` | `#858585` | `#858585` |
| `SelectionBoundStroke` | `#64b5f6` | `#FDF7C3` |
| `SelectionDotStroke` | `#64b5f6` | `#f2ae00` |
| `SelectionDotFill` | `#FFFFFF` | `#1E1E1E` |

For the use of each color, refer to [Rendering](rendering.md#colors).

### Add a canvas color

1. Add a value to `ThemeColor` with a light color and a dark color.
2. In the canvas code, call `ThemeManager.getInstance().getColorCode(ThemeColor.<value>)`.

## CSS

The HTML parts use two style systems at the same time:

| System | Source | Output | Gradle task |
|---|---|---|---|
| Sass | `src/main/sass/` | `main.css` | `compileSass` |
| Tailwind CSS 3 | `src/main/css/tailwind.css`, `tailwind.config.js` | `tailwind.css` | `compileTailwind` |

Both tasks write to `build/processedResources/js/main/`. `jsProcessResources` depends on both tasks. For the build details, refer to [Build system](build-system.md).

`index.html` loads `tailwind.css` first and `main.css` second.

### Sass files

| File | Content |
|---|---|
| `main.scss` | The entry file. It imports the fonts and the other files. |
| `style/variables.scss` | The fonts, the sizes and the z-index values. |
| `style/theme-utils.scss` | The `theme` mixin. |
| `style/theme.scss` | `$themeMap`, the CSS variables for both modes. |
| `style/shared.scss`, `style/commons.scss`, `style/animation.scss` | Shared rules and animations. |
| `style/header/` | The navigation bar. |
| `style/modal/` | The modals, the menus, the text editor and the keyboard shortcut panel. |

### Theme CSS variables

`$themeMap` in `style/theme.scss` maps each CSS variable to two values: the light value and the dark value.

```scss
$themeMap: (
    --workspace-bg-color: (#FFF, #121212),
    ...
);

:root {
    @include theme($themeMap);
}
```

The `theme` mixin writes the first value for `:root` and `:root.light`. It writes the second value for `:root.dark`. The variables are set on `body`.

Some files have their own `$themeMap`, for example `style/modal/recent-project-modal.scss`.

Use the variables in Sass with `var(--name)`. Use them in Tailwind classes with an arbitrary value, for example `bg-[var(--workspace-bg-color)]`.

### Add a theme color for CSS

1. Add a line to `$themeMap`: `--my-color: (<light>, <dark>),`.
2. Use `var(--my-color)` in Sass or in a Tailwind class.

### Tailwind configuration

`tailwind.config.js` sets these values:

| Setting | Value |
|---|---|
| `content` | The HTML in `src/main/resources` and all Kotlin files in `src` and `libs`. |
| `darkMode` | `class`. The `dark` class on `<html>` turns on the `dark:` variants. |
| `fontFamily.mono` | `'Jetbrains Mono'`, Menlo, `'Courier New'`, Courier, monospace |
| `fontFamily.ui` | `'Roboto'`, Arial, serif |
| `spacing` | `nav` 48px, `shape-tools` 250px, `canvas-left` 33px, `canvas-top` 18px |
| `zIndex` | The same values as the z-index variables in `variables.scss`. |

`src/main/css/tailwind.css` adds the utilities `h-nav`, `top-nav`, `w-shape-tools` and `right-shape-tools`.

Tailwind finds class names in the Kotlin files. Write the full class name as one string, for example `classes("flex-1", "overflow-y-auto")`. Tailwind cannot find a class name that the code builds at run time.

### Shared values

Some values are in both Sass and Tailwind. When you change one, change the other.

| Value | Sass | Tailwind |
|---|---|---|
| Navigation bar height | `$nav-height` | `spacing.nav`, `h-nav`, `top-nav` |
| Axis size | `$canvas-left`, `$canvas-top` | `spacing.canvas-left`, `spacing.canvas-top` |
| Fonts | `$monospaceFont`, `$uiTextFont` | `fontFamily.mono`, `fontFamily.ui` |
| Z-index | `$...-zindex` | `zIndex` |

The canvas font is also in Kotlin: `DrawingInfoController.DEFAULT_FONT`.

### The hidden class

Sass and Tailwind both define `.hidden`:

| Source | Rule |
|---|---|
| `style/shared.scss` | Moves the element out of the screen. It does not change `display`. |
| Tailwind | `display: none` |

Both rules apply to an element with the class `hidden`. `CssClass.HIDE` in `ui-toolbar` uses this class to hide the Format panel. The hidden `textarea` for the clipboard also uses it. Refer to [Known issues](known-issues.md).

## Fonts

| Font | Use | Source |
|---|---|---|
| JetBrains Mono | The canvas and the text editor. | `src/main/resources/fonts/`, loaded by `main.scss` |
| Roboto | The UI text. | Google Fonts, loaded by `main.scss` |

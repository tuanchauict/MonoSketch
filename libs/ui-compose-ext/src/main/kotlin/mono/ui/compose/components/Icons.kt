/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.ui.compose.components

import androidx.compose.runtime.Composable
import mono.ui.compose.ext.Svg
import mono.ui.compose.ext.SvgPath
import mono.ui.compose.ext.fill
import mono.ui.compose.ext.size
import mono.ui.compose.ext.strokeColor
import mono.ui.compose.ext.svgPathStrokeWidth
import mono.ui.compose.ext.viewBox

/* ktlint-disable max-line-length */
object Icons {
    @Composable
    fun Cross(iconSize: Int = 16) {
        Svg(
            attrs = {
                size(iconSize, iconSize)
                viewBox(1024, 1024)
                fill("currentColor")
            }
        ) {
            SvgPath(
                "M512.481 421.906 850.682 84.621c25.023-24.964 65.545-24.917 90.51.105s24.917 65.545-.105 90.51L603.03 512.377 940.94 850c25.003 24.984 25.017 65.507.033 90.51s-65.507 25.017-90.51.033L512.397 602.764 174.215 940.03c-25.023 24.964-65.545 24.917-90.51-.105s-24.917-65.545.105-90.51l338.038-337.122L84.14 174.872c-25.003-24.984-25.017-65.507-.033-90.51s65.507-25.017 90.51-.033L512.48 421.906z"
            )
        }
    }

    @Composable
    fun ChevronDown(iconSize: Int = 16) {
        Svg(
            attrs = {
                size(iconSize, iconSize)
                viewBox(16, 16)
                fill("currentColor")
            }
        ) {
            SvgPath(
                "M1.646 4.646a.5.5 0 0 1 .708 0L8 10.293l5.646-5.647a.5.5 0 0 1 .708.708l-6 6a.5.5 0 0 1-.708 0l-6-6a.5.5 0 0 1 0-.708z"
            )
        }
    }

    @Composable
    fun Remove(iconSize: Int = 16) {
        Svg(
            attrs = {
                size(iconSize, iconSize)
                viewBox(512, 512)
                fill("currentColor")
            }
        ) {
            SvgPath(
                "M490.667 85.333H379.325l-7.012-28.062C363.905 23.609 333.658 0 298.965 0h-85.931c-34.693 0-64.94 23.609-73.348 57.273l-7.012 28.06H21.333C9.551 85.333 0 94.885 0 106.667S9.551 128 21.333 128h22.488l17.974 323.53C63.683 485.452 91.744 512 125.719 512h260.565c33.975 0 62.036-26.548 63.924-60.468L468.183 128h22.484c11.782 0 21.333-9.551 21.333-21.333 0-11.782-9.551-21.334-21.333-21.334zM181.081 67.614c3.663-14.664 16.838-24.948 31.954-24.948h85.931c15.116 0 28.291 10.284 31.953 24.946l4.428 17.721H176.653l4.428-17.719zm226.527 381.549c-.63 11.311-9.993 20.17-21.324 20.17H125.719c-11.33 0-20.694-8.859-21.324-20.172L86.554 128h338.897l-17.843 321.163z"
            )
            SvgPath(
                "M170.667 170.667c-11.782 0-21.333 9.551-21.333 21.333v213.333c0 11.782 9.551 21.333 21.333 21.333 11.782 0 21.333-9.551 21.333-21.333V192c0-11.782-9.551-21.333-21.333-21.333zM256 170.667c-11.782 0-21.333 9.551-21.333 21.333v213.333c0 11.782 9.551 21.333 21.333 21.333s21.333-9.551 21.333-21.333V192c0-11.782-9.551-21.333-21.333-21.333zM341.333 170.667C329.551 170.667 320 180.218 320 192v213.333c0 11.782 9.551 21.333 21.333 21.333 11.782 0 21.333-9.551 21.333-21.333V192c.001-11.782-9.551-21.333-21.333-21.333z"
            )
        }
    }

    @Composable
    fun Inbox(iconSize: Int = 16) {
        Svg(
            attrs = {
                size(iconSize, iconSize)
                viewBox(800, 800)
                fill("none")
            }
        ) {
            SvgPath(
                "M771 442 644 145c-7-17-24-28-43-28H199c-19 0-36 11-43 28L29 442m742 0v186c0 51-42 93-93 93H122c-51 0-93-42-93-93V442m742 0H586c-26 0-47 21-47 47v46c0 26-21 47-46 47H307c-25 0-46-21-46-47v-46c0-26-21-47-46-47H29m242-208 244-2m-291 94h337"
            ) {
                attr("stroke", "currentColor")
                attr("stroke-linecap", "round")
                attr("stroke-linejoin", "round")
                attr("stroke-width", "50")
            }
        }
    }

    @Composable
    fun OpenInNewTab(iconSize: Int = 16) {
        Svg(
            attrs = {
                size(iconSize, iconSize)
                viewBox(512, 512)
                fill("currentColor")
            }
        ) {
            SvgPath(
                "M352 0a32 32 0 0 0-23 55l42 41-170 169a32 32 0 0 0 46 46l169-170 41 42c10 9 23 12 35 7s20-17 20-30V32c0-18-14-32-32-32H352zM80 32C36 32 0 68 0 112v320c0 44 36 80 80 80h320c44 0 80-36 80-80V320a32 32 0 1 0-64 0v112c0 9-7 16-16 16H80c-9 0-16-7-16-16V112c0-9 7-16 16-16h112a32 32 0 1 0 0-64H80z"
            )
        }
    }

    @Composable
    fun Folder(iconSize: Int = 16) {
        Svg(
            attrs = {
                size(iconSize, iconSize)
                viewBox(576, 512)
                fill("currentColor")
            }
        ) {
            SvgPath(
                "M64 480h384c35 0 64-29 64-64V160c0-35-29-64-64-64H288c-10 0-20-5-26-13l-19-25a64 64 0 0 0-51-26H64C29 32 0 61 0 96v320c0 35 29 64 64 64z"
            )
        }
    }

    @Composable
    fun FolderOpen(iconSize: Int = 16) {
        Svg(
            attrs = {
                size(iconSize, iconSize)
                viewBox(576, 512)
                fill("currentColor")
            }
        ) {
            SvgPath(
                "M384 480h48c11 0 22-6 28-16l112-192a32 32 0 0 0-28-48H144c-11 0-22 6-28 16L48 357V96c0-9 7-16 16-16h118c4 0 8 2 11 5l26 26c21 21 50 33 80 33h117c9 0 16 7 16 16v32h48v-32c0-35-29-64-64-64H299c-17 0-34-7-46-19l-26-26a64 64 0 0 0-46-19H64C29 32 0 61 0 96v320c0 35 29 64 64 64h320z"
            )
        }
    }

    @Composable
    fun Warning(iconSize: Int = 16) {
        Svg(
            attrs = {
                size(iconSize, iconSize)
                viewBox(512, 512)
                fill("currentColor")
            }
        ) {
            SvgPath(
                "M256 32c14 0 27 8 35 20l216 368a40 40 0 0 1-35 60H40a40 40 0 0 1-34-60L222 52c7-12 20-20 34-20zm0 128c-13 0-24 11-24 24v112a24 24 0 1 0 48 0V184c0-13-11-24-24-24zm32 224a32 32 0 1 0-64 0 32 32 0 1 0 64 0z"
            )
        }
    }

    @Composable
    fun NewFile(iconSize: Int = 16) {
        Svg(
            attrs = {
                size(iconSize, iconSize)
                viewBox(-64, 0, 384, 512)
                fill("currentColor")
            }
        ) {
            SvgPath(
                "M0 64C0 29 29 0 64 0h160v128c0 18 14 32 32 32h128v288c0 35-29 64-64 64H64c-35 0-64-29-64-64V64zm384 64H256V0l128 128z"
            )
        }
    }

    @Composable
    fun FileImport(iconSize: Int = 16) {
        Svg(
            attrs = {
                size(iconSize, iconSize)
                viewBox(512, 512)
                fill("currentColor")
            }
        ) {
            SvgPath(
                "M288 109v243a32 32 0 1 1-64 0V109l-73 74a32 32 0 0 1-46-46L233 9c13-12 33-12 46 0l128 128a32 32 0 0 1-46 46l-73-74zM64 352h128a64 64 0 0 0 128 0h128c35 0 64 29 64 64v32c0 35-29 64-64 64H64c-35 0-64-29-64-64v-32c0-35 29-64 64-64zm368 104a24 24 0 1 0 0-48 24 24 0 1 0 0 48z"
            )
        }
    }

    @Composable
    fun RoundedCorner(iconSize: Int = 16) {
        Svg(
            attrs = {
                size(374 * iconSize / 512, iconSize)
                viewBox(374, 512)
                strokeColor("currentColor")
                fill("none")
            }
        ) {
            SvgPath(
                "M16 146V55c0-22 17-39 39-39h91M228 16h91c22 0 39 17 39 39v91M146 496H55c-22 0-39-17-39-39v-91M358 366v91c0 22-17 39-39 39h-91"
            ) {
                svgPathStrokeWidth(30f)
            }
        }
    }
}
/* ktlint-enable max-line-length */

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
}
/* ktlint-enable max-line-length */

/*
 * Copyright (c) 2023, tuanchauict
 */

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

repositories {
    google()
    mavenCentral()
}

kotlin {
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }

    sourceSets {
        named("jsMain") {
            kotlin.srcDir("src/main/kotlin")

            dependencies {
                implementation(projects.actionManager)
                implementation(projects.commons)
                implementation(projects.graphicsgeo)
                implementation(projects.htmlDsl)
                implementation(projects.lifecycle)
                implementation(projects.livedata)
                implementation(projects.shape)
                implementation(projects.storeDao)
                implementation(projects.uiAppStateManager)
                implementation(projects.uiComposeExt)
                implementation(projects.uiModal)
                implementation(projects.uiTheme)

                implementation(compose.html.core)
                implementation(compose.runtime)
            }
        }

        named("jsTest") {
            kotlin.srcDir("src/test/kotlin")

            dependencies {
                implementation(libs.kotlin.test.js)
            }
        }
    }
}

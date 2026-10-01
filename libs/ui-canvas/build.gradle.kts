/*
 * Copyright (c) 2023, tuanchauict
 */

plugins {
    kotlin("multiplatform")
}

repositories {
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
                implementation(projects.commons)
                implementation(projects.graphicsgeo)
                implementation(projects.htmlDsl)
                implementation(projects.lifecycle)
                implementation(projects.livedata)
                implementation(projects.monoboard)
                implementation(projects.shapeInteractionBound)
                implementation(projects.uiAppStateManager)
                implementation(projects.uiModal)
                implementation(projects.uiTheme)
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

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
        val jsMain by getting {
            kotlin.srcDir("src/main/kotlin")

            dependencies {
                implementation(projects.actionManager)
                implementation(projects.browserManager)
                implementation(projects.commons)
                implementation(projects.graphicsgeo)
                implementation(projects.keycommand)
                implementation(projects.lifecycle)
                implementation(projects.livedata)
                implementation(projects.monoboard)
                implementation(projects.monobitmap)
                implementation(projects.monobitmapManager)
                implementation(projects.shape)
                implementation(projects.shapeClipboard)
                implementation(projects.shapeSelection)
                implementation(projects.shapeSerialization)
                implementation(projects.statemanager)
                implementation(projects.storeManager)
                implementation(projects.uiAppStateManager)
                implementation(projects.uiCanvas)
                implementation(projects.uiToolbar)
            }
        }

        val jsTest by getting {
            kotlin.srcDir("src/test/kotlin")

            dependencies {
                implementation(libs.kotlin.test.js)
            }
        }
    }
}

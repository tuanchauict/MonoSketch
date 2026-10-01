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
                implementation(libs.kotlin.stdlib.js)

                implementation(projects.actionManager)
                implementation(projects.buildEnvironment)
                implementation(projects.commons)
                implementation(projects.exportShapesModal)
                implementation(projects.graphicsgeo)
                implementation(projects.htmlDsl)
                implementation(projects.keycommand)
                implementation(projects.lifecycle)
                implementation(projects.livedata)
                implementation(projects.monobitmap)
                implementation(projects.monobitmapManager)
                implementation(projects.monoboard)
                implementation(projects.shape)
                implementation(projects.shapeClipboard)
                implementation(projects.shapeInteractionBound)
                implementation(projects.shapeSelection)
                implementation(projects.shapeSerialization)
                implementation(projects.shapesearcher)
                implementation(projects.storeDao)
                implementation(projects.storeManager)
                implementation(projects.uiAppStateManager)
                implementation(projects.uiCanvas)
                implementation(projects.uiModal)
                implementation(projects.uiToolbar)
                implementation(projects.uuid)
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

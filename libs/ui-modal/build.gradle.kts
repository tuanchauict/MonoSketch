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
        val jsMain by getting {
            kotlin.srcDir("src/main/kotlin")

            dependencies {
                implementation(projects.browserManager)
                implementation(projects.commons)
                implementation(projects.htmlDsl)
                implementation(projects.lifecycle)
                implementation(projects.livedata)
                implementation(projects.uiComposeExt)

                implementation(compose.html.core)
                implementation(compose.runtime)
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

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
                implementation(projects.lifecycle)
                implementation(projects.livedata)
                implementation(projects.uiTheme)

                implementation(compose.html.core)
                implementation(compose.runtime)

                implementation(libs.kotlin.stdlib.js)
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

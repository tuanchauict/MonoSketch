/*
 * Copyright (c) 2023, tuanchauict
 */

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
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
                implementation(projects.commons)
                implementation(projects.graphicsgeo)
                implementation(projects.shape)

                implementation(libs.kotlinx.serialization.json)
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

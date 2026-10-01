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
        named("jsMain") {
            kotlin.srcDir("src/main/kotlin")

            dependencies {
                implementation(projects.graphicsgeo)
                implementation(projects.htmlDsl)
                implementation(projects.livedata)
                implementation(projects.shape)

                implementation(libs.kotlinx.serialization.json)

                implementation(libs.kotlin.stdlib.js)
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

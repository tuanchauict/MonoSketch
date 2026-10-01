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
                implementation(projects.commons)
                implementation(projects.graphicsgeo)
                implementation(projects.livedata)
                implementation(projects.monobitmap)
                implementation(projects.uuid)

                implementation(libs.kotlinx.serialization.json)
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

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
                implementation(projects.commons)
                implementation(projects.graphicsgeo)
                implementation(projects.monobitmap)
                implementation(projects.shape)

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

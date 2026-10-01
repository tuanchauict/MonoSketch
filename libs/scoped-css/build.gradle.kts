/*
 * Copyright (c) 2026, tuanchauict
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

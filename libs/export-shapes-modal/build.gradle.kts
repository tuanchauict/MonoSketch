/*
 * Copyright (c) 2023, tuanchauict
 */

plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.graphicsgeo)
    implementation(projects.htmlDsl)
    implementation(projects.lifecycle)
    implementation(projects.livedata)
    implementation(projects.monobitmap)
    implementation(projects.monoboard)
    implementation(projects.shape)

    implementation(libs.kotlin.stdlib.js)
    testImplementation(libs.kotlin.test.js)
}

val compilerType: org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType by ext
kotlin {
    js(compilerType) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }
}

/*
 * Copyright (c) 2023, tuanchauict
 */

plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.commons)
    implementation(projects.graphicsgeo)
    implementation(projects.livedata)
    implementation(projects.monobitmap)
    implementation(projects.uuid)

    implementation(libs.kotlinx.serialization.json)

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

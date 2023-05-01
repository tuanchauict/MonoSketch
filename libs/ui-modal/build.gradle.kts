/*
 * Copyright (c) 2023, tuanchauict
 */

plugins {
    kotlin("js")
    id("org.jetbrains.compose")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.commons)
    implementation(projects.htmlDsl)
    implementation(projects.lifecycle)
    implementation(projects.livedata)

    implementation(compose.html.core)
    implementation(compose.runtime)
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

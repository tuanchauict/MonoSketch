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
    implementation(projects.actionManager)
    implementation(projects.htmlDsl)
    implementation(projects.lifecycle)
    implementation(projects.livedata)
    implementation(projects.storeDao)
    implementation(projects.uiAppStateManager)
    implementation(projects.uiModal)
    implementation(projects.uiTheme)

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

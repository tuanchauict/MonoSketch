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
    implementation(projects.actionManager)
    implementation(projects.htmlDsl)
    implementation(projects.htmlmodal)
    implementation(projects.lifecycle)
    implementation(projects.livedata)
    implementation(projects.uiAppStateManager)
    implementation(projects.uiTheme)

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

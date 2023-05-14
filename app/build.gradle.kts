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
    implementation(projects.browserManager)
    implementation(projects.commons)
    implementation(projects.graphicsgeo)
    implementation(projects.keycommand)
    implementation(projects.lifecycle)
    implementation(projects.livedata)
    implementation(projects.monoboard)
    implementation(projects.monobitmap)
    implementation(projects.monobitmapManager)
    implementation(projects.shape)
    implementation(projects.shapeClipboard)
    implementation(projects.shapeSelection)
    implementation(projects.shapeSerialization)
    implementation(projects.statemanager)
    implementation(projects.storeManager)
    implementation(projects.uiAppStateManager)
    implementation(projects.uiCanvas)
    implementation(projects.uiToolbar)

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

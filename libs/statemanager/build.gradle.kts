/*
 * Copyright (c) 2023, tuanchauict
 */

plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib.js)

    implementation(projects.actionManager)
    implementation(projects.buildEnvironment)
    implementation(projects.commons)
    implementation(projects.exportShapesModal)
    implementation(projects.keycommand)
    implementation(projects.lifecycle)
    implementation(projects.livedata)
    implementation(projects.monobitmap)
    implementation(projects.monobitmapManager)
    implementation(projects.monoboard)
    implementation(projects.shape)
    implementation(projects.shapeClipboard)
    implementation(projects.shapeInteractionBound)
    implementation(projects.shapeSelection)
    implementation(projects.shapesearcher)
    implementation(projects.storeDao)
    implementation(projects.storeManager)
    implementation(projects.uiAppStateManager)
    implementation(projects.uiCanvas)
    implementation(projects.uiModal)
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

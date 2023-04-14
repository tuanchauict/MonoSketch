/*
 * Copyright (c) 2023, tuanchauict
 */

plugins {
    kotlin("js")
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(projects.buildEnvironment)
    implementation(projects.commons)
    implementation(projects.graphicsgeo)
    implementation(projects.monobitmap)

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

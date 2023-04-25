/*
 * Copyright (c) 2023, tuanchauict
 */

import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType

plugins {
    kotlin("js") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("io.miret.etienne.sass") version "1.4.0"
}

group = "com.monosketch"
version = "1.1.0-alpha"

allprojects {
    ext {
        set("compilerType", KotlinJsCompilerType.IR)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.app)
    implementation(projects.lifecycle)

    testImplementation(libs.kotlin.test.js)
}

val compilerType: KotlinJsCompilerType by ext
kotlin {
    js(compilerType) {
        browser {
            binaries.executable()
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }
}

apply(from = "ktlint.gradle")
apply(from = "sass.gradle")

// TODO: Move this into a separate script inside buildSrc
// watchSass. This is not required when running the project.
// Only run this task (along with run task) if you want to see the UI update immediately when
// editing the style
tasks.register<io.miret.etienne.gradle.sass.CompileSass>("watchSass") {
    setSourceDir(project.file("$projectDir/src/main/sass"))
    outputDir = project.file("$buildDir/processedResources/js/main")

    watch()
}

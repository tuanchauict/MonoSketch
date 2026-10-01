/*
 * Copyright (c) 2023, tuanchauict
 */

import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootEnvSpec

plugins {
    kotlin("multiplatform") version "2.4.20"
    kotlin("plugin.serialization") version "2.4.20"
    kotlin("plugin.compose") version "2.4.20" apply false
    id("org.jetbrains.compose") version "1.11.1" apply false
    id("io.miret.etienne.sass") version "1.4.0"
}

group = "com.monosketch"

repositories {
    google()
    mavenCentral()
}

// On a machine behind a TLS-intercepting corporate proxy that can't reach
// nodejs.org/yarnpkg.com directly, pass -PuseSystemNodeJs=true to use a
// locally installed Node.js/Yarn instead of letting the Kotlin/JS plugin
// download them. CI and any machine with normal internet access should
// leave this off so it gets the pinned, known-good versions.
// Every subproject declares its own js {} target, so this must apply to all of them.
if (providers.gradleProperty("useSystemNodeJs").getOrElse("false").toBoolean()) {
    allprojects {
        plugins.withType<NodeJsPlugin> {
            extensions.configure<NodeJsEnvSpec> {
                download.set(false)
            }
        }
        plugins.withType<YarnPlugin> {
            extensions.configure<YarnRootEnvSpec> {
                download.set(false)
            }
        }
    }
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
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")

            dependencies {
                implementation(projects.app)
                implementation(projects.lifecycle)
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(libs.kotlin.test.js)
            }
        }
    }
}

apply(from = "ktlint.gradle")
apply(from = "sass.gradle")
apply(from = "tailwind.gradle")

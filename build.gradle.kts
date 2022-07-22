import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType

plugins {
    kotlin("js") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("io.miret.etienne.sass") version "1.1.2"
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
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
}

apply(from = "ktlint.gradle")
apply(from = "sass.gradle")

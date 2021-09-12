import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension

plugins {
    kotlin("js") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.0"
}

group = "com.monosketch"
version = "1.0.0-alpha"

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

// TODO: Remove this. This is a workaround for running on browser for debugging.
afterEvaluate {
    rootProject.extensions.configure<NodeJsRootExtension> {
        versions.webpackDevServer.version = "4.0.0"
    }
}

apply(from = "ktlint.gradle")

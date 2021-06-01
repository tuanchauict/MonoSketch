plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":graphicsgeo"))
    implementation(project(":livedata"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")

    testImplementation(kotlin("test-js"))
}

val compilerType: org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType by ext
kotlin {
    js(compilerType) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
        binaries.executable()
    }
}

plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":commons"))
    implementation(project(":graphicsgeo"))
    implementation(project(":livedata"))
    implementation(project(":monobitmap"))
    implementation(project(":shape-extra-manager"))
    implementation(project(":uuid"))

    implementation(libs.kotlinx.serialization.json)

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

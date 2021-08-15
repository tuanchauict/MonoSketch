plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":graphicsgeo"))
    implementation(project(":html-ext"))
    implementation(project(":lifecycle"))
    implementation(project(":livedata"))
    implementation(project(":monobitmap"))
    implementation(project(":monoboard"))
    implementation(project(":shape"))

    implementation(libs.kotlinx.html)

    implementation(kotlin("stdlib-js"))
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

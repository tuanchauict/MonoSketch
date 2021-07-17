plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":commons"))
    implementation(project(":graphicsgeo"))
    implementation(project(":keycommand"))
    implementation(project(":lifecycle"))
    implementation(project(":livedata"))
    implementation(project(":shape"))

    implementation(libs.kotlinx.html)

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

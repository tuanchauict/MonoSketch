plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
}

dependencies {
    implementation(project(":graphicsgeo"))
    implementation(project(":lifecycle"))
    implementation(project(":livedata"))
    implementation(project(":monobitmap"))
    implementation(project(":monoboard"))
    implementation(project(":shape"))

    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.2")

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

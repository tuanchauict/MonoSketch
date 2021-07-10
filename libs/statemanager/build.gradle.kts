plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(project(":commons"))
    implementation(project(":export-shapes-modal"))
    implementation(project(":graphicsgeo"))
    implementation(project(":htmlcanvas"))
    implementation(project(":htmlmodal"))
    implementation(project(":htmltoolbar"))
    implementation(project(":keycommand"))
    implementation(project(":lifecycle"))
    implementation(project(":livedata"))
    implementation(project(":monobitmap"))
    implementation(project(":monobitmap-manager"))
    implementation(project(":monoboard"))
    implementation(project(":shape"))
    implementation(project(":shape-clipboard"))
    implementation(project(":shape-interaction-bound"))
    implementation(project(":shapesearcher"))

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

plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))

    implementation(project(":build-environment"))
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
    implementation(project(":shape-selection"))
    implementation(project(":shape-serialization"))
    implementation(project(":shapesearcher"))
    implementation(project(":store-manager"))

    testImplementation(libs.kotlin.test.js)
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

plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.commons)
    implementation(projects.graphicsgeo)
    implementation(projects.htmlcanvas)
    implementation(projects.htmltoolbar)
    implementation(projects.keycommand)
    implementation(projects.lifecycle)
    implementation(projects.livedata)
    implementation(projects.monoboard)
    implementation(projects.monobitmap)
    implementation(projects.monobitmapManager)
    implementation(projects.shape)
    implementation(projects.shapeClipboard)
    implementation(projects.shapeSelection)
    implementation(projects.shapeSerialization)
    implementation(projects.statemanager)
    implementation(projects.storeManager)

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

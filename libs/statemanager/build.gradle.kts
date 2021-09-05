plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib.js)

    implementation(projects.buildEnvironment)
    implementation(projects.commons)
    implementation(projects.exportShapesModal)
    implementation(projects.graphicsgeo)
    implementation(projects.htmlDsl)
    implementation(projects.htmlcanvas)
    implementation(projects.htmlmodal)
    implementation(projects.htmltoolbar)
    implementation(projects.keycommand)
    implementation(projects.lifecycle)
    implementation(projects.livedata)
    implementation(projects.monobitmap)
    implementation(projects.monobitmapManager)
    implementation(projects.monoboard)
    implementation(projects.shape)
    implementation(projects.shapeClipboard)
    implementation(projects.shapeInteractionBound)
    implementation(projects.shapeSelection)
    implementation(projects.shapeSerialization)
    implementation(projects.shapesearcher)
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

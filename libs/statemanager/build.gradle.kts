plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(project(":commons"))
    implementation(project(":graphicsgeo"))
    implementation(project(":htmlcanvas"))
    implementation(project(":htmlmodal"))
    implementation(project(":htmltoolbar"))
    implementation(project(":keycommand"))
    implementation(project(":lifecycle"))
    implementation(project(":livedata"))
    implementation(project(":monobitmap"))
    implementation(project(":monoboard"))
    implementation(project(":shape"))
    implementation(project(":shape-interaction-bound"))
    implementation(project(":shapesearcher"))

    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.2")

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

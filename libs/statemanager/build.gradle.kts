plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.2")
    implementation(project(":commons"))
    implementation(project(":graphicsgeo"))
    implementation(project(":lifecycle"))
    implementation(project(":livedata"))
    implementation(project(":shape"))
    implementation(project(":shapesearcher"))
    implementation(project(":monoboard"))
    implementation(project(":shape-interaction-bound"))
    implementation(project(":htmlcanvas"))
    implementation(project(":monobitmap"))
    implementation(project(":htmlmodal"))
    implementation(project(":keycommand"))

    testImplementation(kotlin("test-js"))
}

kotlin {
    js(LEGACY) {
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

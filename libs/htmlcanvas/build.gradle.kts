plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.2")
    implementation(project(":commons"))
    implementation(project(":graphicsgeo"))
    implementation(project(":monoboard"))
    implementation(project(":lifecycle"))
    implementation(project(":livedata"))
    implementation(project(":shape-interaction-bound"))

    testImplementation(kotlin("test-js"))
}

kotlin {
    js(IR) {
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

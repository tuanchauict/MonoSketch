plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(project(":livedata"))
    implementation(project(":shape"))
    implementation(project(":shapesearcher"))
    implementation(project(":monoboard"))
    implementation(project(":htmlcanvas"))
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

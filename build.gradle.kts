plugins {
    kotlin("js") version "1.4.21-2"
}

group = "com.monoflow"
version = "0.0.1"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(project(":app"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")

    testImplementation(kotlin("test-js"))
}

kotlin {
    js(IR) {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
}

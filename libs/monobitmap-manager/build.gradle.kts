plugins {
    kotlin("js")
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(project(":commons"))
    implementation(project(":graphicsgeo"))
    implementation(project(":monobitmap"))
    implementation(project(":shape"))

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

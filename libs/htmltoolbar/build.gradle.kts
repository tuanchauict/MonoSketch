plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.actionManager)
    implementation(projects.htmlDsl)
    implementation(projects.htmlmodal)
    implementation(projects.lifecycle)
    implementation(projects.livedata)
    implementation(projects.theme)

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
    }
}

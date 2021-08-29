import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType

plugins {
    kotlin("js") version "1.5.20"
    kotlin("plugin.serialization") version "1.5.0"
}

group = "com.monosketch"
version = "0.0.1"

allprojects {
    ext {
        set("compilerType", KotlinJsCompilerType.IR)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":app"))
    implementation(project(":lifecycle"))

    testImplementation(libs.kotlin.test.js)
}

val compilerType: KotlinJsCompilerType by ext
kotlin {
    js(compilerType) {
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

// TODO: Remove this. This is a workaround for running on browser for debugging.
afterEvaluate {
    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        versions.webpackDevServer.version = "4.0.0"
    }
}

val ktlint = configurations.create("ktlint")
dependencies {
    add("ktlint", "com.pinterest:ktlint:0.42.1")
}

tasks.create("ktlint", JavaExec::class) {
    description = "Check Kotlin code style."
    main = "com.pinterest.ktlint.Main"
    classpath = ktlint
    args("**/*.kt")
    // to generate report in checkstyle format prepend following args:
    // "--reporter=plain", "--reporter=checkstyle,output=${buildDir}/ktlint.xml"
    // see https://github.com/pinterest/ktlint#usage for more
}

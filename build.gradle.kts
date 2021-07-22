import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType

plugins {
    kotlin("js") version "1.5.20"
    kotlin("plugin.serialization") version "1.5.0"
}

group = "com.monosketch"
version = "0.0.1"

allprojects {
    ext {
        set("compilerType", KotlinJsCompilerType.LEGACY)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":app"))
    implementation(project(":lifecycle"))

    testImplementation(kotlin("test-js"))
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

val ktlint = configurations.create("ktlint")
dependencies {
    add("ktlint", "com.pinterest:ktlint:0.41.0")
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

/*
 * Copyright (c) 2026, tuanchauict
 */

package org.intellij.lang.annotations

/**
 * A copy of JetBrains' `@Language` annotation, which is not available for Kotlin/JS.
 * IntelliJ recognizes it by its fully-qualified name and injects the language into string
 * literals passed to annotated parameters.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE)
annotation class Language(val value: String, val prefix: String = "", val suffix: String = "")

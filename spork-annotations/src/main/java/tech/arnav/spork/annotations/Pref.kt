package tech.arnav.spork.annotations

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.PROPERTY

@Target(PROPERTY, FIELD)
@Retention(RUNTIME)
annotation class Pref(
    // TODO: default to empty for using the variable's name
    val key: String
)
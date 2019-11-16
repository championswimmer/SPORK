package tech.arnav.spork.annotations

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.PROPERTY

@Target(PROPERTY)
@Retention(RUNTIME)
annotation class Pref(
    val prefKey: String = ""
)
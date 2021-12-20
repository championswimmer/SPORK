package tech.arnav.spork.annotations

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@Target(CLASS)
@Retention(RUNTIME)
annotation class PreferenceFile(
    // TODO: default to empty to use the File's name
    val fileName: String = "",
    // TODO: Allow Context.MODE_XXXX
    val mode: Int = 0
)
package tech.arnav.spork.compiler.extensions

import com.squareup.kotlinpoet.TypeName
import kotlinx.metadata.ClassName

fun ClassName.toTypeName(): TypeName {
    val segments = this.split("/")
    val className = segments.last()
    val packageName = segments.dropLast(1).joinToString(".")
    return com.squareup.kotlinpoet.ClassName(packageName, className)
}
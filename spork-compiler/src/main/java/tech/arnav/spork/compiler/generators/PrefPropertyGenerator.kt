package tech.arnav.spork.compiler.generators

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.metadata.ImmutableKmProperty
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import kotlinx.metadata.KmClassifier
import tech.arnav.spork.annotations.Pref
import tech.arnav.spork.compiler.DEFAULT_BOOLEAN
import tech.arnav.spork.compiler.DEFAULT_FLOAT
import tech.arnav.spork.compiler.DEFAULT_INT
import tech.arnav.spork.compiler.DEFAULT_LONG
import tech.arnav.spork.compiler.DEFAULT_STRING
import tech.arnav.spork.compiler.SP_GET_BOOLEAN
import tech.arnav.spork.compiler.SP_GET_FLOAT
import tech.arnav.spork.compiler.SP_GET_INT
import tech.arnav.spork.compiler.SP_GET_LONG
import tech.arnav.spork.compiler.SP_GET_STRING
import tech.arnav.spork.compiler.SP_SET_BOOLEAN
import tech.arnav.spork.compiler.SP_SET_FLOAT
import tech.arnav.spork.compiler.SP_SET_INT
import tech.arnav.spork.compiler.SP_SET_LONG
import tech.arnav.spork.compiler.SP_SET_STRING
import tech.arnav.spork.compiler.VAR_PREFS
import tech.arnav.spork.compiler.extensions.toTypeName

@KotlinPoetMetadataPreview
class PrefPropertyGenerator(kmProperty: ImmutableKmProperty, annotation: Pref) {
    private val typeName =
        (kmProperty.returnType.classifier as KmClassifier.Class).name.toTypeName()

    private val propertySpec = PropertySpec.builder(
        kmProperty.name,
        typeName,
        KModifier.OVERRIDE
    ).mutable()

    private val getterFunc
        get() = when (typeName.toString()) {
            Int::class.qualifiedName -> SP_GET_INT
            String::class.qualifiedName -> SP_GET_STRING
            Long::class.qualifiedName -> SP_GET_LONG
            Boolean::class.qualifiedName -> SP_GET_BOOLEAN
            Float::class.qualifiedName -> SP_GET_FLOAT
            else -> SP_GET_INT
        }
    private val setterFunc
        get() = when (typeName.toString()) {
            Int::class.qualifiedName -> SP_SET_INT
            String::class.qualifiedName -> SP_SET_STRING
            Long::class.qualifiedName -> SP_SET_LONG
            Boolean::class.qualifiedName -> SP_SET_BOOLEAN
            Float::class.qualifiedName -> SP_SET_FLOAT
            else -> SP_SET_INT
        }

    private val getDefaultValue
        get() = when (typeName.toString()) {
            Int::class.qualifiedName -> DEFAULT_INT
            String::class.qualifiedName -> DEFAULT_STRING
            Long::class.qualifiedName -> DEFAULT_LONG
            Boolean::class.qualifiedName -> DEFAULT_BOOLEAN
            Float::class.qualifiedName -> DEFAULT_FLOAT
            else -> DEFAULT_INT
        }


    private val getterSpec = FunSpec.getterBuilder()
        .addStatement(
            """
            return ${VAR_PREFS}.${getterFunc}("${annotation.key}", ${getDefaultValue})
        """.trimIndent()
        )

    private val setterSpec = FunSpec.setterBuilder()
        .addParameter("value", typeName)
        .addStatement("""${VAR_PREFS}.edit().${setterFunc}("${annotation.key}", value).apply()""")

    fun generateSpec(): PropertySpec {
        propertySpec.getter(getterSpec.build())
        propertySpec.setter(setterSpec.build())
        return propertySpec.build()
    }

}
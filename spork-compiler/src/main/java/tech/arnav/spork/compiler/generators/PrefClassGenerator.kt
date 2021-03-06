package tech.arnav.spork.compiler.generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import tech.arnav.spork.annotations.Pref
import tech.arnav.spork.annotations.PreferenceFile
import tech.arnav.spork.compiler.extensions.toTypeName
import java.lang.IllegalArgumentException
import java.util.*
import javax.lang.model.element.Element

@KotlinPoetMetadataPreview
class PrefClassGenerator(val prefClassElement: Element) {

    private val metadata = prefClassElement.getAnnotation(Metadata::class.java)
    private val kmMetadata = metadata.toImmutableKmClass()

    private val typeSpecBuilder = TypeSpec.classBuilder("${prefClassElement.simpleName}Impl")
    private val constructorSpec = FunSpec.constructorBuilder()
        .addParameter(ParameterSpec("context", ClassName("android.content", "Context")))
        .build()

    // TODO: default to empty to use the File's name
    private val prefFileName = prefClassElement.getAnnotation(PreferenceFile::class.java).fileName

    private val prefFileVarSpec = PropertySpec.builder(
        "prefs",
        ClassName("android.content", "SharedPreferences")
    ).initializer("context.getSharedPreferences(\"${prefFileName}\", Context.MODE_PRIVATE)").build()

    private fun addPrefFields() {
        val prefFields = prefClassElement.enclosedElements.filter {
            it.getAnnotation(Pref::class.java) != null
        }.associateBy {
            it.simpleName.substring(0, it.simpleName.indexOf("\$annotations"))
        }
        kmMetadata.properties.forEach { prop ->
            val propName = "get${prop.name.capitalize(Locale.ROOT)}"
            prefFields[propName]?.let { el ->
                typeSpecBuilder.addProperty(
                    PrefPropertyGenerator(
                        prop,
                        el.getAnnotation(Pref::class.java)
                    ).generateSpec()
                )
            }
        }
    }


    fun generateSpec(): TypeSpec {
        typeSpecBuilder.superclass(kmMetadata.name.toTypeName())
            .primaryConstructor(constructorSpec)
            .addProperty(prefFileVarSpec)
            .addOriginatingElement(prefClassElement)

        addPrefFields()
        return typeSpecBuilder.build()
    }
}
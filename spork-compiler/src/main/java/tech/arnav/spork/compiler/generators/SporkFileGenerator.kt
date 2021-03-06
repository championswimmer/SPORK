package tech.arnav.spork.compiler.generators

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.jvm.jvmStatic
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.Filer
import kotlin.reflect.KClass

class SporkFileGenerator {

    private val fileSpecBuilder = FileSpec.builder(
        "tech.arnav.spork",
        "Spork"
    )

    private val sporkObjBuilder = TypeSpec.objectBuilder(
        "Spork"
    )

    private val createFromClassFuncBuilder = FunSpec.builder("create").jvmStatic()
        .addTypeVariable(TypeVariableName("P", Any::class.asClassName()))
        .addAnnotation(
            AnnotationSpec.builder(Suppress::class.asClassName()).addMember("\"UNCHECKED_CAST\"")
                .build()
        )
        .addParameter(
            ParameterSpec.builder(
                "context",
                ClassName("android.content", "Context")
            ).build()
        )
        .addParameter(
            ParameterSpec.builder(
                "prefClass",
                Class::class.asClassName().parameterizedBy(TypeVariableName("P"))
            ).build()
        )
        .returns(TypeVariableName("P"))
        .beginControlFlow("return when (prefClass)")

    private val createFromKClassFuncBuilder = FunSpec.builder("create").jvmStatic()
        .addTypeVariable(TypeVariableName("P", Any::class.asClassName()))
        .addParameter(
            ParameterSpec.builder(
                "context",
                ClassName("android.content", "Context")
            ).build()
        )
        .addParameter(
            ParameterSpec.builder(
                "prefClass",
                KClass::class.asClassName().parameterizedBy(TypeVariableName("P"))
            ).build()
        )
        .returns(TypeVariableName("P"))
        .addStatement("return create(context, prefClass.java)")


    @KotlinPoetMetadataPreview
    fun addPrefFile(prefFileGenerator: PrefFileGenerator) {
        createFromClassFuncBuilder.addStatement(
            "%T::class.java -> %T(context) as P",
            prefFileGenerator.getAbstractClassName(),
            prefFileGenerator.getImplClassName()
        )
        sporkObjBuilder.addOriginatingElement(prefFileGenerator.prefFile)
    }

    fun write(filer: Filer) {
        fileSpecBuilder
            .addType(
                sporkObjBuilder
                    .addFunction(
                        createFromKClassFuncBuilder
                            .build()
                    )
                    .addFunction(
                        createFromClassFuncBuilder
                            .addStatement("else -> throw IllegalArgumentException(\"\${prefClass} not annotated with PreferenceFile\")")
                            .endControlFlow()
                            .build()
                    )
                    .build()
            )
            .build()
            .writeTo(filer)
    }
}
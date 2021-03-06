package tech.arnav.spork.compiler

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.hasAnnotations
import com.squareup.kotlinpoet.metadata.isSynthesized
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import com.squareup.kotlinpoet.metadata.toKotlinClassMetadata
import kotlinx.metadata.KmProperty
import kotlinx.metadata.KmPropertyVisitor
import kotlinx.metadata.jvm.KotlinClassMetadata
import tech.arnav.spork.annotations.Pref
import tech.arnav.spork.annotations.PreferenceFile
import tech.arnav.spork.compiler.generators.PrefClassGenerator
import tech.arnav.spork.compiler.generators.PrefFileGenerator
import tech.arnav.spork.compiler.generators.SporkFileGenerator
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementVisitor
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.ElementScanner8
import javax.lang.model.util.Elements
import javax.lang.model.util.SimpleElementVisitor6
import javax.lang.model.util.SimpleElementVisitor8

@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes(
    value = [
        "tech.arnav.spork.annotations.Pref",
        "tech.arnav.spork.annotations.PreferenceFile"
    ]
)
class PrefProcessor : AbstractProcessor() {
    private lateinit var elementUtils: Elements

    override fun init(p0: ProcessingEnvironment) {
        super.init(p0)
        this.elementUtils = p0.elementUtils
    }

    @KotlinPoetMetadataPreview
    override fun process(
        annotations: MutableSet<out TypeElement>?,
        env: RoundEnvironment?
    ): Boolean {
        if (annotations?.map { it.toString() }?.contains("tech.arnav.spork.annotations.PreferenceFile") != true) {
            return false
        }

        val sporkFileGenerator = SporkFileGenerator()

        env?.getElementsAnnotatedWith(PreferenceFile::class.java)?.forEach { prefFile ->
            PrefFileGenerator(elementUtils, prefFile).let {
                sporkFileGenerator.addPrefFile(it)
                it.write(processingEnv.filer)
            }
        }

        sporkFileGenerator.write(processingEnv.filer)

        return true
    }

}
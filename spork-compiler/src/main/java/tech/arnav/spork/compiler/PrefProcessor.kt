package tech.arnav.spork.compiler

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.sun.tools.javac.code.Symbol
import tech.arnav.spork.annotations.Pref
import tech.arnav.spork.annotations.PreferenceFile
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

@SupportedSourceVersion(SourceVersion.RELEASE_8)
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

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        env: RoundEnvironment?
    ): Boolean {
        println("==============")
        env?.getElementsAnnotatedWith(PreferenceFile::class.java)?.forEach { prefFile ->
            prefFile.enclosedElements
            println(getSrcFiles(arrayOf(prefFile)))
            FileSpec.builder(
                elementUtils.getPackageOf(prefFile).qualifiedName.toString(),
                "${prefFile.simpleName}Impl"
            )
                .addType(
                    TypeSpec.classBuilder("${prefFile.simpleName}Impl")
                        .addOriginatingElement(prefFile)
                        .build()
                )
                .build()
                .writeTo(processingEnv.filer)
        }
        println("==============")

        return true
    }

    private fun getSrcFiles(elements: Array<out Element?>): Set<File> {
        return elements.filterNotNull().mapNotNull { elem ->
            var origin = elem
            while (origin.enclosingElement != null && origin.enclosingElement !is PackageElement) {
                origin = origin.enclosingElement
            }
            val uri = (origin as? Symbol.ClassSymbol)?.sourcefile?.toUri()?.takeIf { it.isAbsolute }
            uri?.let { File(it).canonicalFile }
        }.toSet()
    }
}
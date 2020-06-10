package tech.arnav.spork.compiler.generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.Filer
import javax.lang.model.element.Element
import javax.lang.model.util.Elements

@KotlinPoetMetadataPreview
class PrefFileGenerator(elementUtils: Elements, val prefFile: Element) {

    private val packageName = elementUtils.getPackageOf(prefFile).qualifiedName.toString()
    private val implClassName = "${prefFile.simpleName}Impl"

    private val fileSpecBuilder = FileSpec.builder(
        packageName,
        implClassName
    )
    private val prefClassSpec = PrefClassGenerator(prefFile)

    fun getAbstractClassName(): TypeName = ClassName(packageName, prefFile.simpleName.toString())
    fun getImplClassName(): TypeName = ClassName(packageName, implClassName)

    fun write(filer: Filer) {
        fileSpecBuilder
            .addType(prefClassSpec.generateSpec())
            .build()
            .writeTo(filer)
    }
}
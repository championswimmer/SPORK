package tech.arnav.spork.compiler.generators

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.PropertySpec.Builder
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element

class PrefPropertyGenerator (val prefFieldElement: Element) {

    fun generateSpec() = PropertySpec.builder(
        prefFieldElement.simpleName.toString(),
        prefFieldElement.asType().asTypeName(),
        KModifier.OVERRIDE
    )
        .build()

}
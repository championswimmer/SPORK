package tech.arnav.spork

import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY

@RestrictTo(LIBRARY)
internal object Parsers {
    private val getMatcher = Regex("(get)(.*)")
    private val setMatcher = Regex("(set)(.*)")

    internal fun getterToProp(methodName: String): String {
        val propName = getMatcher.find(methodName)?.groups?.get(2)?.value ?: ""
        return decapitalize(propName)
    }

    internal fun setterToProp(methodName: String): String {
        val propName = setMatcher.find(methodName)?.groups?.get(2)?.value ?: ""
        return decapitalize(propName)
    }

    private fun decapitalize (s: String): String {
        val c = s.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return String(c);
    }
}
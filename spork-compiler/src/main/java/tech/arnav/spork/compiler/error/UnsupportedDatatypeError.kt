package tech.arnav.spork.compiler.error

import com.sun.jdi.InvalidTypeException

class UnsupportedDatatypeError(typeName: String) : InvalidTypeException(
    """Unsupported datatype ${typeName}. Prefs can only be of primitive types"""
) {
}
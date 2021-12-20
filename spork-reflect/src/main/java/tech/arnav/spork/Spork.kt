package tech.arnav.spork

import tech.arnav.spork.annotations.Pref
import tech.arnav.spork.annotations.PreferenceFile
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation


object Spork {

    @JvmStatic
    fun <T : Any> create(context: Context, prefInterface: KClass<T>): T {
        return Proxy.newProxyInstance(prefInterface.java.classLoader, arrayOf(prefInterface.java),
            object : InvocationHandler {

                private val prefs: SharedPreferences
                private val prefMap: Map<String, Pair<String, KType>>

                init {
                    val prefFileAnnotation = prefInterface.findAnnotation<PreferenceFile>()
                        ?: throw IllegalArgumentException("Interface ${prefInterface.qualifiedName} not annotated with PreferenceFile")
                    val fileName = prefFileAnnotation.fileName.takeIf { it.isNotEmpty() } ?: prefInterface.simpleName
                    val validPrefSet = mutableSetOf<String>()

                    prefs = context.getSharedPreferences(fileName, MODE_PRIVATE)
                    prefMap = mapOf(
                        *(prefInterface.declaredMemberProperties.asSequence().map { prop ->

                            prop.findAnnotation<Pref>()?.let {
                                validPrefSet.add(it.key) //TODO: use propname if prefKey empty

                                return@map Pair(prop.name, Pair(it.key, prop.returnType))
                            }

                        }.filterNotNull().toList().toTypedArray())
                    )

                    prefs.edit {
                        prefs.all.keys.asSequence().filter { !validPrefSet.contains(it) }.forEach {
                            Log.d("PREFS", "Removing obsolete pref $it")
                            remove(it)
                        }
                    }
                }


                private fun invokeGetter(prop: String): Any {
                    prefMap[prop]?.let {

                        return when (it.second) {
                            Int::class ->
                                prefs.getInt(it.first, 0)
                            Boolean::class ->
                                prefs.getBoolean(it.first, false)
                            Float::class,
                            Double::class ->
                                prefs.getFloat(it.first, 0.0f)
                            String::class ->
                                prefs.getString(it.first, "")!!
                            else ->
                                prefs.getString(it.first, "")!!

                        }
                    } ?: return "" //FIXME: Figure out a proper return
                }

                private fun invokeSetter(prop: String, value: Any?): Unit {
                    prefMap[prop]?.let {
                        prefs.edit {
                            when (it.second) {
                                Int::class ->
                                    putInt(it.first, value as Int)
                                Boolean::class ->
                                    putBoolean(it.first, value as Boolean)
                                Float::class ->
                                    putFloat(it.first, value as Float)
                                Double::class ->
                                    putFloat(it.first, (value as Double).toFloat())
                                String::class ->
                                    putString(it.first, value as String)
                                else ->
                                    putString(it.first, value?.toString())
                            }
                        }
                    }
                }

                override fun invoke(instance: Any?, method: Method?, args: Array<out Any>?): Any? {


                    if (method?.declaringClass === Any::class.java) {
                        return method.invoke(this, args)
                    }

                    if (method?.name?.startsWith("get") == true) {
                        val propName = Parsers.getterToProp(method.name)
                        return invokeGetter(propName)

                    }

                    if (method?.name?.startsWith("set") == true) {
                        val propName = Parsers.setterToProp(method.name)
                        return invokeSetter(propName, args?.get(0))
                    }

                    return null
                }
            }
        ) as T
    }
}
package me.thuongle.googlebookssearch.util

import android.content.Context
import android.support.annotation.StringRes
import android.support.test.InstrumentationRegistry
import java.lang.reflect.Field
import java.lang.reflect.Modifier


fun getString(@StringRes id: Int, vararg args: Any): String {
    return InstrumentationRegistry.getTargetContext().getString(id, *args)
}

fun getStringFromFile(context: Context, filePath: String): String {
    return context.resources.assets.open(filePath).use {
        it.bufferedReader(Charsets.UTF_8).readText()
    }
}

@Throws(Exception::class)
fun setFinalStatic(field: Field, newValue: Any) {
    field.isAccessible = true

    val modifiersField: Field = try {
        Field::class.java.getDeclaredField("accessFlags")
    } catch (e: NoSuchFieldException) {
        Field::class.java.getDeclaredField("modifiers")
    }

    modifiersField.isAccessible = true
    modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())

    field.set(null, newValue)
}
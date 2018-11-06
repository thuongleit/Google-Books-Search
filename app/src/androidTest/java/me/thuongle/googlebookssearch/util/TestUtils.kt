package me.thuongle.googlebookssearch.util

import android.content.Context
import android.support.annotation.StringRes
import android.support.test.InstrumentationRegistry

fun getString(@StringRes id: Int, vararg args: Any): String {
    return InstrumentationRegistry.getTargetContext().getString(id, *args)
}

fun getStringFromFile(context: Context, filePath: String): String {
    return context.resources.assets.open(filePath).use {
        it.bufferedReader(Charsets.UTF_8).readText()
    }
}
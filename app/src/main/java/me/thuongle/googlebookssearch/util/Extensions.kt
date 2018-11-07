package me.thuongle.googlebookssearch.util

import java.io.UnsupportedEncodingException
import java.net.URLEncoder

@Throws(UnsupportedEncodingException::class)
fun String.encodeUTF8(): String {
    return URLEncoder.encode(this, Charsets.UTF_8.name())
}

fun Map<String, *>.toUrlEncodeUTF8(): String {
    return this
        .map { entry -> "${entry.key.encodeUTF8()}=${entry.value.toString().encodeUTF8()}" }
        .joinToString(separator = "&")
}
package me.thuongle.googlebookssearch.api

import com.google.gson.annotations.SerializedName

data class GoogleVolumeResponse(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("items")
    val items: List<GoogleBook>,
    @SerializedName("totalItems")
    val totalItems: Int = 0
)
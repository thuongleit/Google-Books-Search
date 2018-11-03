package me.thuongle.googlebookssearch.api

import com.google.gson.annotations.SerializedName

data class GoogleBook(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("etag")
    val etag: String,
    @SerializedName("selfLink")
    val selfLink: String,
    @SerializedName("volumeInfo")
    val bookInfo: BookInfo
) {

    data class BookInfo(
        @SerializedName("title")
        val title: String,
        @SerializedName("subtitle")
        val subtitle: String,
        @SerializedName("authors")
        val authors: List<String>,
        @SerializedName("publisher")
        val publisher: String,
        @SerializedName("publishedDate")
        val publishedDate: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("industryIdentifiers")
        val industryIdentifiers: List<IndustryIdentifierInfo>,
        @SerializedName("pageCount")
        val pageCount: Int,
        @SerializedName("dimensions")
        val dimensions: DimensionsInfo,
        @SerializedName("printType")
        val printType: String,
        @SerializedName("categories")
        val categories: List<String>,
        @SerializedName("averageRating")
        val averageRating: Double,
        @SerializedName("ratingsCount")
        val ratingsCount: Int,
        @SerializedName("contentVersion")
        val contentVersion: String,
        @SerializedName("imageLinks")
        val imageLinks: ImageInfo,
        @SerializedName("language")
        val language: String,
        @SerializedName("previewLink")
        val previewLink: String,
        @SerializedName("infoLink")
        val infoLink: String,
        @SerializedName("canonicalVolumeLink")
        val canonicalVolumeLink: String
    )

    data class IndustryIdentifierInfo(
        val type: String,
        val identifier: String
    )

    data class DimensionsInfo(
        @SerializedName("height")
        val height: String,
        @SerializedName("width")
        val width: String,
        @SerializedName("thickness")
        val thickness: String
    )

    data class ImageInfo(
        @SerializedName("smallThumbnail")
        val smallThumbnail: String?,
        @SerializedName("thumbnail")
        val thumbnail: String?,
        @SerializedName("small")
        val small: String?,
        @SerializedName("medium")
        val medium: String?,
        @SerializedName("large")
        val large: String?,
        @SerializedName("extraLarge")
        val extraLarge: String?
    )
}

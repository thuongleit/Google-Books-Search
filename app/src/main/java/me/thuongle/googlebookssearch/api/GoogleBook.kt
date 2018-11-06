package me.thuongle.googlebookssearch.api

import com.google.gson.annotations.SerializedName

data class GoogleBook(
    val kind: String,
    val id: String,
    val etag: String,
    val selfLink: String,
    @SerializedName("volumeInfo")
    val bookInfo: BookInfo
) {

    data class BookInfo(
        val title: String,
        val subtitle: String,
        @SerializedName("authors")
        val authorsList: List<String>,
        val publisher: String,
        val publishedDate: String,
        val description: String,
        val industryIdentifiers: List<IndustryIdentifierInfo>,
        val pageCount: Int,
        val dimensions: DimensionsInfo,
        val printType: String,
        val categories: List<String>,
        val averageRating: Double,
        val ratingsCount: Int,
        val contentVersion: String,
        val imageLinks: ImageInfo,
        val language: String,
        val previewLink: String,
        val infoLink: String,
        val canonicalVolumeLink: String
    ) {
        val authors: String
            // list.toString() --> [Author-A, Author-B] --> Author-A, Author-B
            get() = authorsList.toString().drop(1).dropLast(1)

    }

    data class IndustryIdentifierInfo(
        val type: String,
        val identifier: String
    )

    data class DimensionsInfo(
        val height: String,
        val width: String,
        val thickness: String
    )

    data class ImageInfo(
        val smallThumbnail: String?,
        val thumbnail: String?,
        val small: String?,
        val medium: String?,
        val large: String?,
        val extraLarge: String?
    )
}

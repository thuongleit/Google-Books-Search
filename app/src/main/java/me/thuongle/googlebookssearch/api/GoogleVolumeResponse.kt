package me.thuongle.googlebookssearch.api

data class GoogleVolumeResponse(
    val kind: String,
    val items: List<GoogleBook>,
    val totalItems: Int = 0
) {
    companion object {
        fun createEmpty(): GoogleVolumeResponse {
            return GoogleVolumeResponse(
                kind = "",
                items = arrayListOf(),
                totalItems = 0
            )
        }
    }
}
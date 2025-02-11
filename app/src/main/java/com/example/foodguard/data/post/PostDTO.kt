package com.example.foodguard.data.post

data class PostDTO(
    val description: String = "",
    val serving: Int = 0,
    val image: String,
    val address: String,
    val author_id: String,
    val expiration_date: String,
    val is_delivered: Boolean = false,
    val id: String? = null
) {
    fun toPostModel(): PostModel {
        return PostModel(
            id = id ?: "",
            description = description,
            author_id = author_id,
            image = image,
            address = address,
            serving =  serving,
            expiration_date = expiration_date,
            is_delivered = is_delivered
        )
    }
}
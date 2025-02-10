package com.example.foodguard.data.post

import androidx.room.Embedded
import androidx.room.Relation
import com.example.foodguard.data.user.UserModel

data class PostWithAuthor(
    @Embedded val post: PostModel,
    @Relation(
        entity = UserModel::class,
        parentColumn = "author_id",
        entityColumn = "id"
    ) val author: UserModel
) {
}
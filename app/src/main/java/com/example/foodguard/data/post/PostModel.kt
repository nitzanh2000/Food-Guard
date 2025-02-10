package com.example.foodguard.data.post

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.foodguard.data.user.UserModel
import java.util.UUID

@Entity(
    tableName = "post",
    foreignKeys = [ForeignKey(
        entity = UserModel::class,
        parentColumns = ["id"],
        childColumns = ["author_id"]
    )]
)
data class PostModel(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "author_id") val author_id: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "image") val image : String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "serving") val serving: Int,
    @ColumnInfo(name = "expiration_date") val expiration_date: String
) {

    fun toPostDto(): PostDTO {
        return PostDTO(
            id = id,
            description = description,
            author_id = author_id,
            image = image,
            address = address,
            serving =  serving,
            expiration_date = expiration_date
        )
    }
}



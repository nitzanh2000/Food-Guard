package com.example.foodguard.data.user;
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.auth.FirebaseAuth

@Entity(tableName = "user")
data class UserModel(
    @PrimaryKey val id: String = "",
    val email: String,
    val display_name: String,
    val profile_picture: String?
) {
    fun toUserDto() : UserDTO {
        return UserDTO(
            id = id,
            email = email,
            display_name = display_name,
            profile_picture = profile_picture
        )
    }
    companion object {
        fun fromFirebaseAuth(): UserModel {
            val user = FirebaseAuth.getInstance().currentUser

            return UserModel(
                id = user?.uid!!,
                email = user.email!!,
                display_name = user.displayName!!,
                profile_picture = ""
            )
        }
    }
}

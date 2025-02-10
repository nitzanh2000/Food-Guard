package com.example.foodguard.data.user;

data class UserDTO(
    val email: String = "",
    val display_name: String = "",
    val profile_picture : String? = "",
    val id: String? = null
) {
    fun toUserModel(): UserModel {
        return UserModel(
            id=id ?: "",
            email = email,
            display_name = display_name,
            profile_picture = profile_picture
        )
    }
}
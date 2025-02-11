package com.example.foodguard.data.user;
import android.content.Context
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodguard.utils.downloadImageAndConvertToBase64
import com.example.foodguard.utils.encodeImageToBase64
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException

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

        suspend fun fromFirebaseAuth(context: Context): UserModel {
            val user = FirebaseAuth.getInstance().currentUser
            var userBase64Image = ""

            if (user?.photoUrl != null) {
                val userProfile = user.photoUrl!!

                 userBase64Image = withContext(Dispatchers.IO) {
                    downloadImageAndConvertToBase64Suspend(userProfile.toString(), context)
                }
            }

            return UserModel(
                id = user!!.uid,
                email = user!!.email!!,
                display_name = user!!.displayName!!,
                profile_picture = userBase64Image
            )
        }

        suspend fun downloadImageAndConvertToBase64Suspend(imageUrl: String, context: Context): String {
            return suspendCancellableCoroutine { continuation ->
                downloadImageAndConvertToBase64(imageUrl, context) { base64String ->
                    continuation.resume(base64String ?: "") { throwable ->
                        continuation.resumeWithException(throwable)
                    }
                }
            }
        }

    }
}

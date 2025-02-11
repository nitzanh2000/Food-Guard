package com.example.foodguard.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*

fun downloadImageAndConvertToBase64(imageUrl: String, context: Context, callback: (String?) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder().url(imageUrl).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Download", "Failed to download image", e)
            callback(null) // Return null in case of failure
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.let { responseBody ->
                responseBody.byteStream().use { inputStream ->
                    val file = File(context.cacheDir, "firebase_profile.jpg")
                    val outputStream = FileOutputStream(file)
                    inputStream.copyTo(outputStream)
                    outputStream.close()

                    // Convert file to URI
                    val imageUri = Uri.fromFile(file)

                    // Encode to Base64
                    val base64String = encodeImageToBase64(imageUri, context)
                    callback(base64String) // Return the Base64 string
                }
            } ?: callback(null) // Return null if response body is empty
        }
    })
}

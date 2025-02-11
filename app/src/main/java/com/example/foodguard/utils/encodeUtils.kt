package com.example.foodguard.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

fun decodeBase64ToImage(base64: String): Bitmap {
    val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

fun encodeImageToBase64(uri: Uri, context: Context): String {
    val inputStream = context.contentResolver.openInputStream(uri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)

    // Reduce the dimensions of the image
    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 480, 480, true) // Adjust width/height

    // Compress the Bitmap
    val compressedStream = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, compressedStream) // Lower quality to 30%
    val compressedByteArray = compressedStream.toByteArray()

    // Convert to Base64
    return Base64.encodeToString(compressedByteArray, Base64.DEFAULT)
}
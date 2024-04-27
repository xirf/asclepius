package com.dicoding.asclepius.helper

import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

class MediaStorageHelper(
    private val context: Context
) {
    fun getImageFromGallery(filename: String): Uri? {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME}=?"
        val selectionArgs = arrayOf(filename)
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        var uri: Uri? = null
        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(idColumn)
                uri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
            }
        }

        return uri
    }

    fun saveImageToGallery(uri: Uri): String {
        val filename = "asclepius_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Asclepius")
            }
        }

        val contentResolver = ContextWrapper(context).contentResolver
        val imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        imageUri?.let {
            contentResolver.openOutputStream(it).use { outputStream ->
                contentResolver.openInputStream(uri).use { inputStream ->
                    inputStream?.copyTo(outputStream!!)
                }
            }
        }

        return filename
    }

}
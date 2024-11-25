package com.jwd.lunchvote.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File

@Suppress("DEPRECATION")
object ImageBitmapFactory {
  fun createBitmapFromUri(context: Context,  uri: Uri): Bitmap =
    if (Build.VERSION.SDK_INT < 29) {
      MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
      val source = ImageDecoder.createSource(context.contentResolver, uri)
      ImageDecoder.decodeBitmap(source)
    }

  fun createByteArrayFromUri(context: Context, uri: Uri, fileName: String): ByteArray {
    val imageBitmap = ImageBitmapFactory.createBitmapFromUri(context, uri)
    val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "images")
      .apply { if (!exists()) mkdirs() }
    val file = File(directory, "$fileName.jpg").apply {
      outputStream().use { outputStream ->
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
      }
    }
    return file.readBytes()
  }
}
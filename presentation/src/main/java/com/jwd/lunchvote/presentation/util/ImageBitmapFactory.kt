package com.jwd.lunchvote.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

@Suppress("DEPRECATION")
class ImageBitmapFactory {
  fun createBitmapFromUri(context: Context,  uri: Uri): Bitmap =
    if (Build.VERSION.SDK_INT < 29) {
      MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
      val source = ImageDecoder.createSource(context.contentResolver, uri)
      ImageDecoder.decodeBitmap(source)
    }
}
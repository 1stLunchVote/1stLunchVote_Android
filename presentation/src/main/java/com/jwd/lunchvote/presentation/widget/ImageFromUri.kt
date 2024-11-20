package com.jwd.lunchvote.presentation.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.util.ImageBitmapFactory
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import java.io.File

@Composable
fun ImageFromUri(
  uri: Uri,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.Crop,
  context: Context = LocalContext.current
) {
  if (uri.toString().startsWith("http")) {
    CoilImage(
      imageModel = { uri.toString() },
      modifier = modifier,
      imageOptions = ImageOptions(
        contentScale = contentScale
      ),
      loading = {
        Box(
          modifier = Modifier.background(MaterialTheme.colorScheme.background),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(
            color = MaterialTheme.colorScheme.outlineVariant,
            strokeWidth = 2.dp,
          )
        }
      }
    )
  } else if (uri.toString().startsWith("content")) {
    Image(
      bitmap = ImageBitmapFactory.createBitmapFromUri(context, uri).asImageBitmap(),
      contentDescription = "Image",
      modifier = modifier,
      contentScale = contentScale
    )
  } else if (File(uri.toString()).exists()) {
    Image(
      bitmap = BitmapFactory.decodeFile(uri.toString()).asImageBitmap(),
      contentDescription = "Image",
      modifier = modifier,
      contentScale = contentScale
    )
  } else {
    Box(
      modifier = modifier.background(MaterialTheme.colorScheme.background),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = stringResource(R.string.image_placeholder),
        color = MaterialTheme.colorScheme.outline,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelMedium
      )
    }
  }
}

@Composable
fun ImageWithUploadButton(
  uri: Uri,
  onImageChange: (Uri) -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 160.dp,
  onError: () -> Unit = {},
) {
  val albumLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri: Uri? ->
    if (imageUri != null) onImageChange(imageUri)
    else onError()
  }

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.BottomEnd
  ) {
    ImageFromUri(
      uri = uri,
      modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
    )
    IconButton(
      onClick = { albumLauncher.launch("image/*") },
      colors = IconButtonColors(
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
      )
    ) {
      Icon(
        Icons.Rounded.Edit,
        contentDescription = null,
        modifier = Modifier.size(28.dp)
      )
    }
  }
}
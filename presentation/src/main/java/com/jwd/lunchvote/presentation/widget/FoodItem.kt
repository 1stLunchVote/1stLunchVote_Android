package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.theme.colorSuccess
import com.jwd.lunchvote.presentation.util.clickableWithoutEffect
import com.jwd.lunchvote.presentation.util.conditional
import com.jwd.lunchvote.presentation.util.glow
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun FoodItem(
  foodItem: FoodItem,
  modifier: Modifier = Modifier,
  size: Dp = 100.dp,
  onClick: () -> Unit = {}
) {
  Column(
    modifier = modifier.clickableWithoutEffect(onClick),
    verticalArrangement = Arrangement.spacedBy(4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    FoodImage(
      imageUrl = foodItem.food.imageUrl,
      status = foodItem.status,
      size = size
    )
    Text(
      foodItem.food.name,
      style = MaterialTheme.typography.bodyLarge
    )
  }
}

@Composable
private fun FoodImage(
  imageUrl: String,
  status: FoodItem.Status,
  modifier: Modifier = Modifier,
  size: Dp = 100.dp
) {
  val color = when (status) {
    FoodItem.Status.DEFAULT -> MaterialTheme.colorScheme.outline
    FoodItem.Status.LIKE -> colorSuccess
    FoodItem.Status.DISLIKE -> MaterialTheme.colorScheme.error
  }

  Box(
    modifier = modifier
      .conditional(status == FoodItem.Status.LIKE){
        glow(MaterialTheme.colorScheme.secondary, size.div(2), 16.dp)
      }
      .conditional(status == FoodItem.Status.DISLIKE) { alpha(0.5f) },
  ) {
    CoilImage(
      imageModel = { imageUrl },
      modifier = Modifier
        .size(size)
        .clip(MaterialTheme.shapes.medium)
        .border(2.dp, color, MaterialTheme.shapes.medium),
      imageOptions = ImageOptions(
        contentScale = ContentScale.Crop
      ),
      previewPlaceholder = R.drawable.ic_food_image_temp
    )
    if (status == FoodItem.Status.DISLIKE) {
      Image(
        painterResource(R.drawable.ic_mask_reversed),
        contentDescription = null,
        modifier = Modifier.size(size),
      )
      Image(
        painterResource(R.drawable.ic_mask_outline),
        contentDescription = null,
        modifier = Modifier.size(size)
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun FoodItemDefaultPreview() {
  LunchVoteTheme {
    Row(
      modifier = Modifier.padding(8.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      FoodItem(foodItem = FoodItem(
        food = FoodUIModel(
          name = "햄버거"
        ), status = FoodItem.Status.DEFAULT
      ), onClick = {})
      FoodItem(foodItem = FoodItem(
        food = FoodUIModel(
          name = "햄버거"
        ), status = FoodItem.Status.LIKE
      ), onClick = {})
      FoodItem(foodItem = FoodItem(
        food = FoodUIModel(
          name = "햄버거"
        ), status = FoodItem.Status.DISLIKE
      ), onClick = {})
    }
  }
}
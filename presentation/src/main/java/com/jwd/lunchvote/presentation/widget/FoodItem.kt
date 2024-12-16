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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.jwd.lunchvote.presentation.R
import com.jwd.lunchvote.presentation.model.FoodItem
import com.jwd.lunchvote.presentation.model.FoodUIModel
import com.jwd.lunchvote.presentation.modifier.clickableWithoutEffect
import com.jwd.lunchvote.presentation.modifier.conditional
import com.jwd.lunchvote.presentation.modifier.innerShadow
import com.jwd.lunchvote.presentation.modifier.outerShadow
import com.jwd.lunchvote.presentation.theme.LunchVoteTheme
import com.jwd.lunchvote.presentation.widget.FoodItemDefaults.dummyFood

@Composable
internal fun FoodItem(
  foodItem: FoodItem,
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {}
) {
  Column(
    modifier = modifier.clickableWithoutEffect(onClick),
    verticalArrangement = Arrangement.spacedBy(4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    FoodImage(
      imageUrl = foodItem.food.imageUrl,
      status = foodItem.status
    )
    Text(
      foodItem.food.name,
      style = MaterialTheme.typography.titleSmall
    )
  }
}

@Composable
private fun FoodImage(
  imageUrl: String,
  status: FoodItem.Status,
  modifier: Modifier = Modifier
) {
  val color = when (status) {
    FoodItem.Status.DEFAULT -> MaterialTheme.colorScheme.outline
    FoodItem.Status.LIKE -> MaterialTheme.colorScheme.secondary
    FoodItem.Status.DISLIKE -> MaterialTheme.colorScheme.error
  }

  Box(
    modifier = modifier
      .size(100.dp)
      .conditional(status == FoodItem.Status.LIKE) {
        outerShadow(
          color = MaterialTheme.colorScheme.secondary,
          shape = MaterialTheme.shapes.medium,
          offsetY = 0.dp,
          blur = 8.dp
        ).innerShadow(
          color = MaterialTheme.colorScheme.secondary,
          shape = MaterialTheme.shapes.medium,
          offsetY = 0.dp,
          blur = 8.dp
        )
      }
      .conditional(status == FoodItem.Status.DISLIKE) { alpha(0.5f) },
  ) {
    ImageFromUri(
      uri = imageUrl.toUri(),
      modifier = Modifier
        .matchParentSize()
        .clip(MaterialTheme.shapes.medium)
        .border(2.dp, color, MaterialTheme.shapes.medium)
    )
    if (status == FoodItem.Status.DISLIKE) {
      Image(painterResource(R.drawable.ic_mask_reversed), null, Modifier.matchParentSize())
      Image(painterResource(R.drawable.ic_mask_outline), null, Modifier.matchParentSize())
    }
  }
}

internal object FoodItemDefaults {

  fun dummyFood(index: Int) = FoodUIModel(
    name = "${index}번 음식"
  )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
  LunchVoteTheme {
    Row(
      modifier = Modifier.padding(8.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      FoodItem(
        foodItem = FoodItem(
          food = dummyFood(1),
          status = FoodItem.Status.DEFAULT
        )
      )
      FoodItem(
        foodItem = FoodItem(
          food = dummyFood(2),
          status = FoodItem.Status.LIKE
        )
      )
      FoodItem(
        foodItem = FoodItem(
          food = dummyFood(3),
          status = FoodItem.Status.DISLIKE
        )
      )
    }
  }
}
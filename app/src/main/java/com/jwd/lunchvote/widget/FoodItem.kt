package com.jwd.lunchvote.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.core.ui.theme.colorSuccess
import com.jwd.lunchvote.core.ui.util.circleShadow
import com.jwd.lunchvote.domain.entity.FoodStatus
import com.jwd.lunchvote.model.FoodUIModel

@Composable
fun FoodItem(
  food: FoodUIModel,
  onClick: () -> Unit = {}
) {
  Column(
    modifier = Modifier.clickable(
      interactionSource = remember{ MutableInteractionSource() },
      indication = null,
      onClick = onClick
    ),
    verticalArrangement = Arrangement.spacedBy(4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    when(food.status) {
      FoodStatus.DEFAULT -> {
        Image(
          painterResource(R.drawable.ic_food_image_temp),
          null,
          modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .alpha(0.8f)
        )
      }
      FoodStatus.LIKE -> {
        Image(
          painterResource(R.drawable.ic_food_image_temp),
          null,
          modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, colorSuccess, RoundedCornerShape(16.dp))
            .circleShadow(colorSuccess, blurRadius = 8.dp)
        )
      }
      FoodStatus.DISLIKE -> {
        Box(
          modifier = Modifier.alpha(0.5f)
        ) {
          Image(
            painterResource(R.drawable.ic_food_image_temp),
            null,
              modifier = Modifier.size(100.dp)
          )
          Image(
            painterResource(R.drawable.ic_mask_reversed),
            null,
            modifier = Modifier.size(100.dp),
          )
          Image(
            painterResource(R.drawable.ic_mask_outline),
            null,
            modifier = Modifier.size(100.dp)
          )
        }
      }
    }
    Text(
      food.name,
      style = MaterialTheme.typography.bodyLarge
    )
  }
}

@Preview(showBackground = true)
@Composable
fun FoodItemDefaultPreview(){
  LunchVoteTheme {
    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      FoodItem(
        FoodUIModel(
          foodId = 0L,
          imageUrl = "",
          name = "햄버거",
          status = FoodStatus.DEFAULT
        )
      )
      FoodItem(
        FoodUIModel(
          foodId = 0L,
          imageUrl = "",
          name = "햄버거",
          status = FoodStatus.LIKE
        )
      )
      FoodItem(
        FoodUIModel(
          foodId = 0L,
          imageUrl = "",
          name = "햄버거",
          status = FoodStatus.DISLIKE
        )
      )
    }
  }
}
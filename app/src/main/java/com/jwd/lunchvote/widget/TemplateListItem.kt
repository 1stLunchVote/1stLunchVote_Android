package com.jwd.lunchvote.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.R
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.domain.entity.Template
import com.jwd.lunchvote.model.TemplateUIModel

@Composable
fun TemplateListItem(
  template: TemplateUIModel,
  onClick: () -> Unit,
) {
  val shape = RoundedCornerShape(8.dp)
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(shape)
      .background(MaterialTheme.colorScheme.background, shape)
      .border(BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant), shape)
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 20.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(template.name, style = MaterialTheme.typography.bodyLarge)
        LikeDislike(like = template.like.size, dislike = template.dislike.size)
      }
      Image(painterResource(R.drawable.ic_caret_right), null)
    }
  }
}

@Composable
fun TemplateListButton(
  onClick: () -> Unit
) {
  val shape = RoundedCornerShape(8.dp)
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(shape)
      .background(MaterialTheme.colorScheme.background, shape)
      .border(BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant), shape)
      .clickable { onClick() },
    contentAlignment = Alignment.Center
  ) {
    Image(
      painterResource(R.drawable.ic_add),
      null,
      modifier = Modifier.padding(vertical = 20.dp)
    )
  }
}

@Preview
@Composable
fun TemplateListItemPreview() {
  LunchVoteTheme {
    Surface {
      TemplateListItem(
        TemplateUIModel(
          uid = "",
          name = "스트레스 받을 때(매운 음식)",
          like = listOf(),
          dislike = listOf()
        ),
        {}
      )
    }
  }
}

@Preview
@Composable
fun TemplateListButtonPreview() {
  LunchVoteTheme {
    Surface {
      TemplateListButton {}
    }
  }
}
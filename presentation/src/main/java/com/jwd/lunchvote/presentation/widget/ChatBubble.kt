package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.core.ui.theme.colorNeutral90
import com.jwd.lunchvote.presentation.model.ChatUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel

@Composable
fun ChatBubble(
  chat: ChatUIModel,
  member: MemberUIModel,
  isMine: Boolean,
  modifier: Modifier = Modifier,
  onClickMember: (MemberUIModel) -> Unit = {}
) {
  when (chat.type) {
    ChatUIModel.Type.DEFAULT -> Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = if (isMine) Arrangement.End else Arrangement.spacedBy(8.dp, alignment = Alignment.Start),
    ) {
      if (isMine) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp, 0.dp, 20.dp, 20.dp))
            .border(
              2.dp,
              MaterialTheme.colorScheme.onBackground,
              RoundedCornerShape(20.dp, 0.dp, 20.dp, 20.dp)
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = chat.message,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
          )
        }
      } else {
        MemberProfile(
          member = member,
          onClick = onClickMember
        )
        Column(
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = chat.userName,
            style = MaterialTheme.typography.titleSmall
          )
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(0.dp, 20.dp, 20.dp, 20.dp))
              .border(
                2.dp,
                MaterialTheme.colorScheme.onBackground,
                RoundedCornerShape(0.dp, 20.dp, 20.dp, 20.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = chat.message,
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
            )
          }
        }
      }
    }
    else -> Box(
      modifier = modifier.fillMaxWidth(),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .size(256.dp, 24.dp)
          .clip(RoundedCornerShape(100))
          .background(color = colorNeutral90),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = chat.message,
          color = MaterialTheme.colorScheme.background,
          style = MaterialTheme.typography.titleSmall
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun ChatBubblePreview() {
  LunchVoteTheme {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      ChatBubble(
        chat = ChatUIModel(
          message = "투표 방이 생성되었습니다.",
          type = ChatUIModel.Type.SYSTEM
        ),
        member = MemberUIModel(),
        isMine = false
      )
      ChatBubble(
        chat = ChatUIModel(
          message = "김철수님이 입장하였습니다.",
          type = ChatUIModel.Type.SYSTEM
        ),
        member = MemberUIModel(),
        isMine = false
      )
      ChatBubble(
        chat = ChatUIModel(
          userName = "김철수",
          message = "안녕하세요",
          type = ChatUIModel.Type.DEFAULT
        ),
        member = MemberUIModel(
          type = MemberUIModel.Type.OWNER
        ),
        isMine = false
      )
      ChatBubble(
        chat = ChatUIModel(
          userName = "김철수",
          message = "안녕하세요",
          type = ChatUIModel.Type.DEFAULT
        ),
        member = MemberUIModel(),
        isMine = true
      )

      ChatBubble(
        chat = ChatUIModel(
          userName = "김철수",
          message = "김철수님이 추방되었습니다.",
          type = ChatUIModel.Type.SYSTEM
        ),
        member = MemberUIModel(),
        isMine = false
      )
    }
  }
}
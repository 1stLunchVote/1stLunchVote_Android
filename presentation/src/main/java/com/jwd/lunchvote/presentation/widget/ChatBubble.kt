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
import com.jwd.lunchvote.presentation.model.LoungeChatUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import com.jwd.lunchvote.presentation.model.type.MemberStatusUIType
import com.jwd.lunchvote.presentation.model.type.MessageUIType
import com.jwd.lunchvote.presentation.model.type.SendStatusUIType

@Composable
fun ChatBubble(
  chat: LoungeChatUIModel,
  member: MemberUIModel,
  isMine: Boolean,
  modifier: Modifier = Modifier,
  onClickProfile: (MemberUIModel) -> Unit = {}
) {
  when (chat.messageType) {
    MessageUIType.NORMAL -> Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = if (isMine) Arrangement.End else Arrangement.spacedBy(8.dp, alignment = Alignment.Start),
    ) {
      if (isMine) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp, 0.dp, 20.dp, 20.dp))
            .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(20.dp, 0.dp, 20.dp, 20.dp)),
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
          onClick = onClickProfile
        )
        Box(
          modifier = Modifier
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(0.dp, 20.dp, 20.dp, 20.dp))
            .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(0.dp, 20.dp, 20.dp, 20.dp)),
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
          text = when (chat.messageType) {
            MessageUIType.CREATE -> "투표 방이 생성되었습니다."
            MessageUIType.JOIN -> "${chat.userName}님이 입장했습니다."
            MessageUIType.EXIT -> "${chat.userName}님이 퇴장했습니다."
            else -> ""
          },
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
        chat = LoungeChatUIModel(
          messageType = MessageUIType.CREATE
        ),
        member = MemberUIModel(),
        isMine = false
      )
      ChatBubble(
        chat = LoungeChatUIModel(
          userName = "김철수",
          messageType = MessageUIType.JOIN
        ),
        member = MemberUIModel(),
        isMine = false
      )
      ChatBubble(
        chat = LoungeChatUIModel(
          userName = "김철수",
          message = "안녕하세요",
          messageType = MessageUIType.NORMAL,
          sendStatus = SendStatusUIType.SUCCESS
        ),
        member = MemberUIModel(
          status = MemberStatusUIType.JOINED
        ),
        isMine = false
      )
      ChatBubble(
        chat = LoungeChatUIModel(
          userName = "김철수",
          message = "안녕하세요",
          messageType = MessageUIType.NORMAL,
          sendStatus = SendStatusUIType.SUCCESS
        ),
        member = MemberUIModel(),
        isMine = true
      )
      ChatBubble(
        chat = LoungeChatUIModel(
          userName = "김철수",
          message = "안녕하세요",
          messageType = MessageUIType.NORMAL,
          sendStatus = SendStatusUIType.SUCCESS
        ),
        member = MemberUIModel(
          status = MemberStatusUIType.READY
        ),
        isMine = false
      )

      ChatBubble(
        chat = LoungeChatUIModel(
          userName = "김철수",
          messageType = MessageUIType.EXIT
        ),
        member = MemberUIModel(),
        isMine = false
      )
    }
  }
}
package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.core.ui.theme.colorNeutral90
import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.presentation.model.ChatUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ChatBubble(
  chat: ChatUIModel,
  member: MemberUIModel,
  isMine: Boolean,
  modifier: Modifier = Modifier,
  previousChat: ChatUIModel? = null,
  nextChat: ChatUIModel? = null,
  onClickMember: (MemberUIModel) -> Unit = {}
) {
  val isSameUserWithPrevious = previousChat != null
    && previousChat.type != ChatUIModel.Type.SYSTEM
    && chat.userId == previousChat.userId
  val isSameTimeWithNext = nextChat != null
    && nextChat.type != ChatUIModel.Type.SYSTEM
    && chat.userId == nextChat.userId
    && chat.createdAt.year == nextChat.createdAt.year
    && chat.createdAt.month == nextChat.createdAt.month
    && chat.createdAt.dayOfMonth == nextChat.createdAt.dayOfMonth
    && chat.createdAt.hour == nextChat.createdAt.hour
    && chat.createdAt.minute == nextChat.createdAt.minute

  val timeFormatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREA)
  val round = 20.dp

  when (chat.type) {
    ChatUIModel.Type.DEFAULT -> Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = if (isMine) Arrangement.End else Arrangement.spacedBy(8.dp, alignment = Alignment.Start),
    ) {
      if (isMine) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.Bottom
        ) {
          val messageShape = if (isSameUserWithPrevious) RoundedCornerShape(round, round, round, round)
          else RoundedCornerShape(round, 0.dp, round, round)

          Text(
            text = chat.createdAt.format(timeFormatter),
            modifier = Modifier
              .padding(bottom = 4.dp)
              .alpha(if (isSameTimeWithNext) 0f else 1f),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.labelMedium
          )
          Box(
            modifier = Modifier
              .clip(messageShape)
              .border(2.dp, MaterialTheme.colorScheme.onBackground, messageShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = chat.message,
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
            )
          }
        }
      } else {
        MemberProfile(
          member = member,
          modifier = Modifier.alpha(if (isSameUserWithPrevious) 0f else 1f),
          onClick = onClickMember
        )
        Column(
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          if (isSameUserWithPrevious.not()) {
            Text(
              text = chat.userName,
              style = MaterialTheme.typography.titleSmall
            )
          }
          ReversedRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
          ) {
            val messageShape = if (isSameUserWithPrevious) RoundedCornerShape(round, round, round, round)
            else RoundedCornerShape(0.dp, round, round, round)

            Text(
              text = chat.createdAt.format(timeFormatter),
              modifier = Modifier
                .padding(bottom = 4.dp)
                .alpha(if (isSameTimeWithNext) 0f else 1f),
              color = MaterialTheme.colorScheme.outline,
              style = MaterialTheme.typography.labelMedium
            )
            Box(
              modifier = Modifier
                .clip(messageShape)
                .border(2.dp, MaterialTheme.colorScheme.onBackground, messageShape),
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
    }
    else -> Box(
      modifier = modifier.fillMaxWidth(),
      contentAlignment = Alignment.Center
    ) {
      ReversedRow(
        modifier = Modifier
          .width(256.dp)
          .clip(RoundedCornerShape(100))
          .background(color = colorNeutral90)
          .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Text(
          text = chat.message,
          color = MaterialTheme.colorScheme.background,
          style = MaterialTheme.typography.titleSmall
        )
        Text(
          text = chat.userName,
          color = MaterialTheme.colorScheme.background,
          overflow = TextOverflow.Ellipsis,
          maxLines = 1,
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
        .padding(24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      ChatBubble(
        chat = ChatUIModel(
          message = Chat.CREATE_SYSTEM_MESSAGE,
          type = ChatUIModel.Type.SYSTEM
        ),
        member = MemberUIModel(),
        isMine = false
      )
      ChatBubble(
        chat = ChatUIModel(
          userName = "김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수",
          message = Chat.JOIN_SYSTEM_MESSAGE,
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
          message = "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요",
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
          message = "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요",
          type = ChatUIModel.Type.DEFAULT
        ),
        member = MemberUIModel(),
        isMine = true
      )
      ChatBubble(
        chat = ChatUIModel(
          userName = "김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수김철수",
          message = Chat.EXILE_SYSTEM_MESSAGE,
          type = ChatUIModel.Type.SYSTEM
        ),
        member = MemberUIModel(),
        isMine = false
      )
    }
  }
}
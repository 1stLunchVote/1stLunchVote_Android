package com.jwd.lunchvote.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme
import com.jwd.lunchvote.domain.entity.Chat
import com.jwd.lunchvote.domain.entity.Member
import com.jwd.lunchvote.domain.entity.Member.Type.READY
import com.jwd.lunchvote.domain.entity.User
import com.jwd.lunchvote.presentation.mapper.asUI
import com.jwd.lunchvote.presentation.model.ChatUIModel
import com.jwd.lunchvote.presentation.model.MemberUIModel
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 말풍선
 *
 * @param chat: 채팅
 * @param member: 채팅을 보낸 멤버
 * @param isMine: 내가 보낸 메시지인지 여부
 * @param modifier: ChatBubble에 적용될 Modifier
 * @param previousChat: 이전 채팅(위에 있는 채팅)
 * @param nextChat: 다음 채팅(아래에 있는 채팅)
 * @param onClickMember: 멤버 사진을 클릭했을 때 호출되는 콜백
 */
@Composable
fun ChatBubble(
  chat: ChatUIModel,
  isMine: Boolean,
  modifier: Modifier = Modifier,
  member: MemberUIModel? = null,
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
    ChatUIModel.Type.DEFAULT -> if (isMine) {
      Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.End),
        verticalAlignment = Alignment.Bottom
      ) {
        Text(
          text = chat.createdAt.format(timeFormatter),
          modifier = Modifier
            .padding(bottom = 4.dp)
            .alpha(if (isSameTimeWithNext) 0f else 1f),
          color = MaterialTheme.colorScheme.outline,
          style = MaterialTheme.typography.labelMedium
        )
        MessageBubble(
          message = chat.message,
          shape = if (isSameUserWithPrevious) RoundedCornerShape(round, round, round, round)
          else RoundedCornerShape(round, 0.dp, round, round)
        )
      }
    } else {
      Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.Start)
      ) {
        MemberProfile(
          member = member!!,  // TODO: 해결 방법 찾기
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
            Text(
              text = chat.createdAt.format(timeFormatter),
              modifier = Modifier
                .padding(bottom = 4.dp)
                .alpha(if (isSameTimeWithNext) 0f else 1f),
              color = MaterialTheme.colorScheme.outline,
              style = MaterialTheme.typography.labelMedium
            )
            MessageBubble(
              message = chat.message,
              shape = if (isSameUserWithPrevious) RoundedCornerShape(round, round, round, round)
              else RoundedCornerShape(0.dp, round, round, round)
            )
          }
        }
      }
    }
    else -> SystemMessage(
      chat = chat,
      modifier = modifier
    )
  }
}

@Composable
private fun MessageBubble(
  message: String,
  shape: Shape,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(shape)
      .border(2.dp, MaterialTheme.colorScheme.onBackground, shape),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = message,
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
    )
  }
}

@Composable
private fun SystemMessage(
  chat: ChatUIModel,
  modifier: Modifier = Modifier
) {
  val width = 256.dp
  val shape = RoundedCornerShape(100)
  val color = MaterialTheme.colorScheme.outlineVariant

  Box(
    modifier = modifier.fillMaxWidth(),
    contentAlignment = Alignment.Center
  ) {
    ReversedRow(
      modifier = Modifier
        .width(width)
        .clip(shape)
        .background(color)
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

@Preview(showBackground = true)
@Composable
private fun ChatBubblePreview() {
  val user1 = User("", "", "김철수", "", 0L, 0L)
  val user2 = User("", "", "김영희", "", 0L, 0L)
  val user3 = User("", "", "김영수", "", 0L, 0L)
  val member1 = Member.Builder("", user1).owner().build()
  val member2 = Member.Builder("", user2).build().copy(type = READY)
  val member3 = Member.Builder("", user3).build()

  LunchVoteTheme {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      ChatBubble(
        chat = Chat.Builder("")
          .create()
          .build()
          .asUI(),
        isMine = false
      )
      ChatBubble(
        chat = Chat.Builder("")
          .join(member2.userName)
          .build()
          .asUI(),
        isMine = false
      )
      ChatBubble(
        chat = Chat.Builder("")
          .member(member2)
          .message("안녕하세요")
          .build()
          .asUI(),
        member = member2.asUI(),
        isMine = false
      )
      ChatBubble(
        chat = Chat.Builder("")
          .join(member3.userName)
          .build()
          .asUI(),
        isMine = false
      )
      ChatBubble(
        chat = Chat.Builder("")
          .member(member3)
          .message("안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요")
          .build()
          .asUI(),
        member = member3.asUI(),
        isMine = false
      )
      ChatBubble(
        chat = Chat.Builder("")
          .member(member1)
          .message("안녕하세요")
          .build()
          .asUI(),
        member = member1.asUI(),
        isMine = true
      )
      ChatBubble(
        chat = Chat.Builder("")
          .member(member1)
          .message("안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요")
          .build()
          .asUI(),
        member = MemberUIModel(),
        isMine = true
      )
      ChatBubble(
        chat = Chat.Builder("")
          .exile(member3.userName)
          .build()
          .asUI(),
        isMine = false
      )
    }
  }
}
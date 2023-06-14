package com.jwd.lunchvote.widget

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jwd.lunchvote.core.ui.theme.LunchVoteTheme

@Composable
fun ChatBubble(
    message: String,
    isMine: Boolean = false,
    profileImage: String?,
    isReady: Boolean = false,
    sendStatus: Int = 0,
    configuration : Configuration = LocalConfiguration.current
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isMine) {
            Spacer(modifier = Modifier.weight(1f))

            if (sendStatus != 0){
                Text(text = "전송중", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.width(8.dp))
            }

            Surface(
                shape = RoundedCornerShape(20.dp, 0.dp, 20.dp, 20.dp),
                color = MaterialTheme.colorScheme.background,
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(vertical = 14.dp)
                )
            }
        } else {
            Surface(
                shape = RoundedCornerShape(0.dp, 20.dp, 20.dp, 20.dp),
                color = MaterialTheme.colorScheme.background,
                border = BorderStroke(2.dp, Color.Black),
                modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 100.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(vertical = 14.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = CircleShape,
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.background,
                border = BorderStroke(width = 2.dp, color = if (isReady) Color.Red else MaterialTheme.colorScheme.outline)
            ) {
                AsyncImage(
                    model = profileImage,
                    contentDescription = "chat_profile",
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ChatBubblePreview(){
    LunchVoteTheme {
        ChatBubble(
            message = "안녕하세요",
            isMine = true,
            profileImage = null,
            sendStatus = 1
        )
    }
}
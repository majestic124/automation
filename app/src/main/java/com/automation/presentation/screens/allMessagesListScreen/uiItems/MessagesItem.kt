package com.automation.presentation.screens.allMessagesListScreen.uiItems

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.automation.R
import com.automation.domain.models.MessageItem
import com.automation.domain.models.MessageType
import com.automation.presentation.screens.allMessagesListScreen.utils.DateMapper
import com.automation.presentation.ui.theme.RawColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MessagesItem(
    modifier: Modifier = Modifier,
    state: MessageItem
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colorScheme.secondaryContainer)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (state.messageType == MessageType.Notification.value) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = colorScheme.onSecondaryContainer
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sms),
                    contentDescription = null,
                    tint = colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSecondaryContainer
                        )
                    ) {
                        append(state.sender)
                    }
                },
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 2.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(
                    id = if (state.isFailed) R.drawable.ic_error_outline_18
                    else R.drawable.ic_check_18
                ),
                contentDescription = null,
                tint = if (state.isFailed) colorScheme.onError
                else RawColors.green.getValue("500")
            )
        }
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Normal,
                        color = colorScheme.onSecondaryContainer
                    )
                ) {
                    val content = state.senderTitle?.let { title -> "$title | ${state.message}" } ?: state.message
                    append(content)
                }
            },
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Normal,
                        color = colorScheme.onSecondaryContainer
                    )
                ) {
                    val messageTimeReceivedContent = DateMapper.formatMessageTime(state.receivedAt)
                    append(messageTimeReceivedContent)
                }
            },
            style = MaterialTheme.typography.labelSmall
        )
    }
}
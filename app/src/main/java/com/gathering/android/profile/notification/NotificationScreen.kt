package com.gathering.android.profile.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gathering.android.common.FullScreenBottomSheet
import com.gathering.android.common.composables.CustomActionButton
import com.gathering.android.databinding.ScreenNotificationBinding
import com.gathering.android.ui.theme.GatheringTheme
import com.gathering.android.ui.theme.CustomBackgroundColor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationScreen : FullScreenBottomSheet() {

    lateinit var binding: ScreenNotificationBinding


    @Inject
    lateinit var viewModel: NotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                GatheringTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NotificationScreenComposeView()
                    }
                }
            }
        }
    }

    @Composable
    fun NotificationScreenComposeView() {

        val notifications = generateDummyNotifications()
        val scrollState = rememberLazyListState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                state = scrollState, modifier = Modifier.weight(1f)
            ) {
                items(notifications) { notification ->
                    NotificationItem(notification = notification)
                }

                item {
                    if (notifications.isEmpty()) {
                        NoDataToShowText()
                    }
                }
            }

            CustomActionButton(
                isLoading = false, text = "Setting", onClick = { /*TODO*/ },
                modifier = Modifier
                    .height(60.dp)
                    .width(170.dp),
            )
        }
    }

    @Composable
    private fun NoDataToShowText() {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            text = "No notifications available",
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }

    @Composable
    fun NotificationItem(notification: Notification) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {},
            colors = CardDefaults.cardColors(
                containerColor = CustomBackgroundColor, contentColor = Color.Black
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = notification.title, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = notification.message)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = notification.time)
            }
        }
    }

    private fun generateDummyNotifications(): List<Notification> {
        return List(20) { index ->
            Notification(
                title = "Event #$index",
                message = "This is the message for Event #$index.",
                time = "10:00 AM"
            )
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun Preview() {
        NotificationScreenComposeView()
    }
}
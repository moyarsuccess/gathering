package com.gathering.android.event.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gathering.android.common.composables.CircularImageView
import com.gathering.android.common.toImageUrl
import com.gathering.android.event.model.Attendee

@Composable
fun AttendeeItem(attendee: Attendee) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        CircularImageView(imageUri = attendee.imageName?.toImageUrl(), size = 50.dp) {}
        Text(
            style = TextStyle(textAlign = TextAlign.Left, fontSize = 14.sp),
            text = attendee.email ?: "",
            modifier = Modifier,
            fontWeight = FontWeight.Bold,
        )
    }
}
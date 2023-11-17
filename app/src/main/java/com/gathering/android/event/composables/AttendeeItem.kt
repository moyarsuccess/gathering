package com.gathering.android.event.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gathering.android.common.composables.CircularImageView
import com.gathering.android.common.toImageUrl
import com.gathering.android.event.eventdetail.AcceptType
import com.gathering.android.event.model.Attendee
import com.gathering.android.ui.theme.CustomGreen
import com.gathering.android.ui.theme.CustomOrange

@Composable
fun AttendeeItem(attendee: Attendee) {
    val textColor = when (attendee.accepted) {
        AcceptType.Yes.type -> CustomGreen
        AcceptType.No.type -> Color.Red
        AcceptType.Maybe.type -> CustomOrange
        else -> Color.Black
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        CircularImageView(imageUri = attendee.imageName?.toImageUrl(), size = 50.dp) {}
        EmailTextView(attendee)
        RsvpTextView(textColor, attendee)
        Spacer(modifier = Modifier.padding(7.dp))
    }
}

@Composable
private fun RsvpTextView(
    textColor: Color,
    attendee: Attendee
) {
    Text(
        style = TextStyle(
            fontSize = 14.sp,
            color = textColor
        ),
        text = attendee.accepted,
        modifier = Modifier
            .wrapContentWidth(),
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun EmailTextView(attendee: Attendee) {
    Text(
        style = TextStyle(
            fontSize = 14.sp
        ),
        text = attendee.email ?: "",
        modifier = Modifier.wrapContentWidth(),
        fontWeight = FontWeight.Bold,
    )
}
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gathering.android.common.composables.CircularImageView
import com.gathering.android.common.toImageUrl
import com.gathering.android.event.eventdetail.AcceptType
import com.gathering.android.event.model.AttendeeModel
import com.gathering.android.ui.theme.CustomGreen
import com.gathering.android.ui.theme.CustomOrange

@Composable
fun AttendeeItem(attendeeModel: AttendeeModel) {
    val textColor = when (attendeeModel.accepted) {
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
        CircularImageView(imageUri = attendeeModel.imageName?.toImageUrl(), size = 50.dp) {}
        EmailTextView(attendeeModel)
        RsvpTextView(textColor, attendeeModel)
        Spacer(modifier = Modifier.padding(7.dp))
    }
}
@Composable
private fun RsvpTextView(
    textColor: Color,
    attendeeModel: AttendeeModel
) {
    Text(
        style = TextStyle(
            fontSize = 14.sp,
            color = textColor
        ),
        text = attendeeModel.accepted,
        modifier = Modifier
            .wrapContentWidth(),
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true)
@Composable
fun AttendeeItemPreview() {
    AttendeeItem(
        attendeeModel = AttendeeModel(accepted = "coming")
    )
}
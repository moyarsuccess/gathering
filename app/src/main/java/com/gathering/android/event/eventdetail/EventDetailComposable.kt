package com.gathering.android.event.eventdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.gathering.android.R
import com.gathering.android.common.NavigationBarPaddingSpacer


@Preview(showBackground = true)
@Composable
fun EventDetailPreview() {
    EventDetail(
        eventName = "Ani",
        eventHostEmail = "animan@gmail.com",
        description = "party",
        photoUrl = "",
        address = "101 Erskine Ave",
        date = "15 Nov 2023",
        time = "15:30",
        acceptType = AcceptType.No,
        onAttachListClicked = {},
        onYesButtonClick = {},
        onNoButtonClick = {},
        onMaybeButtonClick = {}
    )
}

@Composable
fun EventDetail(
    eventName: String,
    eventHostEmail: String,
    description: String,
    photoUrl: String,
    address: String,
    date: String,
    time: String,
    acceptType: AcceptType,
    onAttachListClicked: () -> Unit,
    onYesButtonClick: () -> Unit,
    onNoButtonClick: () -> Unit,
    onMaybeButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
    ) {
        EventImage(photoUrl)
        EventInfo(
            eventName = eventName,
            eventHostEmail = eventHostEmail,
            description = description,
            address = address,
            date = date, time = time,
        )
        AttendeeListButton(onAttachListClicked)
        AcceptTypeAttendee(
            acceptType = acceptType,
            onYesButtonClick = onYesButtonClick,
            onNoButtonClick = onNoButtonClick,
            onMaybeButtonClick = onMaybeButtonClick
        )
        NavigationBarPaddingSpacer()
    }
}

@Composable
fun EventImage(imageUrl: String) {
    Card {
        val painter = if (imageUrl.isEmpty()) {
            painterResource(id = R.drawable.ic_launcher_foreground)
        } else {
            rememberAsyncImagePainter(model = imageUrl)
        }
        Image(
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = "EventPhoto",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}

@Composable
private fun EventInfo(
    eventName: String,
    eventHostEmail: String,
    description: String,
    address: String,
    date: String,
    time: String
) {
    EventTitle(eventName)
    TextInfo(eventHostEmail)
    TextInfo(description)
    TextInfo(address)
    TextInfo(date)
    TextInfo(time)
}

@Composable
fun EventTitle(eventName: String) {
    Text(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        text = eventName,
        modifier = Modifier.padding(start = 10.dp, 40.dp)
    )
}

@Composable
fun TextInfo(text: String) {
    Text(
        fontSize = 16.sp,
        text = text,
        modifier = Modifier.padding(10.dp)
    )
}

@Composable
fun AttendeeButton(
    text: String,
    state: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (state) Color.Gray else Color.DarkGray
    Button(
        shape = RoundedCornerShape(0.dp),
        onClick = { onClick() },
        modifier = Modifier
            .height(60.dp)
            .width(120.dp)
            .padding(5.dp)
            .background(backgroundColor),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        contentPadding = PaddingValues(8.dp),
        content = {
            Text(
                text = text,
                modifier = Modifier.padding(4.dp),
            )
        },
    )
}


@Composable
fun AttendeeListButton(onClick: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.DarkGray
        ),
        onClick = onClick,
        content = {
            Text(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                text = "Click to see list of attendees",
            )
        }
    )
}

@Composable
private fun AcceptTypeAttendee(
    acceptType: AcceptType,
    onYesButtonClick: () -> Unit,
    onNoButtonClick: () -> Unit,
    onMaybeButtonClick: () -> Unit
) {
    Row {
        AttendeeButton(
            text = "YES",
            onClick = {
                onYesButtonClick()
            },
            state = acceptType == AcceptType.Yes
        )
        AttendeeButton(
            text = "NO",
            onClick = {
                onNoButtonClick()
            },
            state = acceptType == AcceptType.No
        )
        AttendeeButton(
            text = "MAYBE",
            onClick = {
                onMaybeButtonClick()
            },
            state = acceptType == AcceptType.Maybe
        )
    }
}


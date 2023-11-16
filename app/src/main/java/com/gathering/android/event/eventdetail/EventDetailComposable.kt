package com.gathering.android.event.eventdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gathering.android.common.composables.NavigationBarPaddingSpacer
import com.gathering.android.event.composables.EventImage
import com.gathering.android.ui.theme.CustomBackgroundColor


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
            .padding(5.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    )
    {
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
private fun EventInfo(
    eventName: String,
    eventHostEmail: String,
    description: String,
    address: String,
    date: String,
    time: String
) {
    EventTitle(eventName, imageVector = Icons.Filled.Person)
    TextInfo(eventHostEmail, imageVector = Icons.Filled.Email)
    TextInfo(description, imageVector = Icons.Filled.Description)
    TextInfo(address, imageVector = Icons.Filled.LocationOn)
    TextInfo(date, imageVector = Icons.Filled.DateRange)
    TextInfo(time, imageVector = Icons.Filled.AccessTime)
}

@Composable
fun EventTitle(eventName: String, imageVector: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        CustomIcon(imageVector)
        Text(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            text = eventName,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Composable
fun TextInfo(text: String, imageVector: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        CustomIcon(imageVector)
        Text(
            fontSize = 16.sp,
            text = text,
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
private fun CustomIcon(imageVector: ImageVector) {
    Card(
        modifier = Modifier.padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = CustomBackgroundColor)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "",
        )
    }
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
            .height(50.dp)
            .width(120.dp)
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
                text = "Click to see the list of attendees",
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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


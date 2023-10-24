package com.gathering.android.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.gathering.android.R

@Composable
@Preview(showBackground = true)
fun ShowProfilePicturePreview() {
    ShowProfilePicture(imageUri = "") {}
}

@Composable
fun ShowProfilePicture(imageUri: String?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(25.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        val painter = if (imageUri.isNullOrEmpty()) {
            painterResource(id = R.drawable.ic_person)
        } else {
            rememberAsyncImagePainter(model = imageUri)
        }
        Image(
            painter = painter,
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(200.dp)
                .background(Color.White, CircleShape)
                .clickable { onClick() }
        )
    }
}

@Composable
fun ShowUserDetails(displayName: String, email: String) {
    Column(
        modifier = Modifier.padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = displayName,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            modifier = Modifier.padding(5.dp),
            text = email,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun HorizontalDivider() {
    Divider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun IconButtonWithTextPreview() {
    ProfileIconButtonWithText(icon = Icons.Filled.Person, text = "favorites") {
    }
}

@Composable
fun ProfileIconButtonWithText(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black
        )
        Text(
            modifier = Modifier.padding(start = 5.dp),
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

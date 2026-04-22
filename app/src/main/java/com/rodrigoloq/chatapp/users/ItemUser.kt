package com.rodrigoloq.chatapp.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.rodrigoloq.chatapp.R
import com.rodrigoloq.chatapp.entities.User
import com.rodrigoloq.chatapp.ui.theme.ChatAppTheme

@Preview(showBackground = true)
@Composable
fun ItemUserViewPreview(){
    ChatAppTheme() {
        ItemUserView(User("asd","asda@asdf.com","asdas",",sdasd","asdas")){}
    }
}

@Composable
fun ItemUserView(user: User, onClick:() -> Unit){
    Box(modifier = Modifier.clickable{
        onClick()
    }, contentAlignment = Alignment.BottomCenter){
        Row(Modifier
            .fillMaxWidth()
            .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user?.image ?: "")
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                placeholder = painterResource(R.drawable.ic_img_profile),
                error = rememberVectorPainter(Icons.Default.BrokenImage),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(text = user.names,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold)
                Text(text = user.email)
            }
        }

        HorizontalDivider()
    }
}
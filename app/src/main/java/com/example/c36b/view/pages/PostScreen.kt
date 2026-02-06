package com.example.c36b.view.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.rememberAsyncImagePainter
import androidx.compose.runtime.Composable
import com.example.c36b.model.Post

@Composable
fun PostScreen(
    post: Post,
    onLike: (Post) -> Unit,
    onComment: (Post) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = post.username, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = post.time, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.content, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            if (post.imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(post.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            Row {
                post.tags.forEach {
                    AssistChip(onClick = {}, label = { Text("#$it") }, modifier = Modifier.padding(end = 6.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { onLike(post) }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Like", tint = Color.Red)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = post.likes.toString())
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { onComment(post) }) {
                    Icon(Icons.Default.Email, contentDescription = "Comment")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = post.comments.size.toString())
                }
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = post.shares.toString())
            }
        }
    }
}
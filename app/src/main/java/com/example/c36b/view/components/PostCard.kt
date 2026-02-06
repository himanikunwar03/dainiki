package com.example.c36b.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
//import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.c36b.model.Post
import coil.compose.rememberAsyncImagePainter
import com.example.c36b.R

@Composable
fun PostCard(
    post: Post, 
    onLike: (Post) -> Unit, 
    onComment: (Post) -> Unit, 
    liked: Boolean = false,
    onBookmark: (Post) -> Unit,
    isBookmarked: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column (modifier = Modifier.padding(12.dp)) {
            Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row (verticalAlignment = Alignment.CenterVertically){
                    Icon(Icons.Default.Person, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = post.username, fontWeight = FontWeight.Bold)
                    }
                }
                Icon(
                    if (isBookmarked) painterResource(R.drawable.outline_bookmark_added_24) else painterResource(id = R.drawable.baseline_bookmark_24),
                    contentDescription = "Bookmark",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBookmark(post) },
                    tint = if (isBookmarked) Color.Blue else Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(post.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)

            Spacer(modifier = Modifier.height(8.dp))
            Text(post.content)

            Spacer(modifier = Modifier.height(8.dp))

            // Use AsyncImagePainter for imageUrl (String)
            Image(
                painter = rememberAsyncImagePainter(post.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                post.tags.forEach {
                    AssistChip(onClick = {}, label = { Text("#$it") }, modifier = Modifier.padding(end = 6.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                IconWithText(
                    Icons.Default.Favorite,
                    post.likes,
                    iconSize = 24.dp,
                    textSize = 16.sp,
                    onClick = { onLike(post) },
                    iconColor = if (liked) Color.Red else Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconWithText(Icons.Default.Email, post.comments.size, iconSize = 24.dp, textSize = 16.sp, onClick = { onComment(post) })
                Spacer(modifier = Modifier.width(12.dp))
                IconWithText(Icons.Default.Share, post.shares, iconSize = 24.dp, textSize = 16.sp)
            }
        }
    }
}

@Composable
fun IconWithText(
    icon: ImageVector,
    count: Int,
    iconSize: Dp = 16.dp,
    textSize: TextUnit = 12.sp,
    onClick: (() -> Unit)? = null,
    iconColor: Color = Color.Gray
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (onClick != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(iconSize).clickable { onClick() }, tint = iconColor)
        } else {
            Icon(icon, contentDescription = null, modifier = Modifier.size(iconSize), tint = iconColor)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text("$count", fontSize = textSize)
    }
}

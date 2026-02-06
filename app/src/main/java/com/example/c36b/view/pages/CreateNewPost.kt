package com.example.c36b.view.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.c36b.repository.UserRepositoryImpl

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateNewPost() {
    var selectedCategory by remember { mutableStateOf("Breaking News") }
    val categories = listOf("Breaking News", "Politics", "Technology", "Business")

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val userRepo = remember { UserRepositoryImpl() }
    val currentUser = userRepo.getCurrentUser()
    var username by remember { mutableStateOf("") }
    LaunchedEffect(currentUser) {
        currentUser?.let {
            userRepo.getUserFromDatabase(it.uid) { success, _, userModel ->
                if (success && userModel != null) {
                    username = userModel.name
                }
            }
        }
    }

    var isPublishing by remember { mutableStateOf(false) }

    LazyColumn (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp))
    {
        item{


        Text("Create Article", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Category Chips
        Text("Category", style = MaterialTheme.typography.labelMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = {
                        Text(category)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2563EB),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFE5E7EB)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
Text(text = "Title", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        // Title Field
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Enter compelling headline...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

//         Content Toolbar
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF2563EB)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2563EB))
            ) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add Image", tint = Color(0xFF2563EB))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Add image from gallery")
            }
        }
        if (imageUri != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
Text(text = "Content", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Content Field
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            placeholder = { Text("Write your article content here...") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(12.dp),
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Username Field
        Text(text = "Author Name", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = username,
            onValueChange = {},
            placeholder = { Text("Enter your name...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tags Field
        Text(text = "Tags (comma separated)", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        var tags by remember { mutableStateOf("") }
        OutlinedTextField(
            value = tags,
            onValueChange = { tags = it },
            placeholder = { Text("e.g. news, world, trending") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Image Picker (already present above as Add Image)
        // You can add logic to display the selected image if needed

        // Submit Button
        Button(
            onClick = {
                isPublishing = true
                val tagList = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val post = com.example.c36b.model.Post(
                    username = username,
                    time = System.currentTimeMillis().toString(),
                    content = content,
                    title = title,
                    tags = tagList,
                    likes = 0,
                    comments = emptyList(),
                    shares = 0,
                    imageUri = imageUri
                )
                val postRepo = com.example.c36b.repository.PostRepositoryImpl()
                postRepo.createPost(context, post) { success, message ->
                    isPublishing = false
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
            enabled = !isPublishing
        ) {
            Text(if (isPublishing) "Publishing..." else "Publish Article", color = Color.White)
        }
    }
    }

}

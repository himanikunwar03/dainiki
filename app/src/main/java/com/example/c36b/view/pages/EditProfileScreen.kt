package com.example.c36b.view.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.c36b.model.UserModel

@Composable
fun EditProfileScreen(
    user: UserModel
) {
    var name by remember { mutableStateOf(user.name) }
    var bio by remember { mutableStateOf(user.bio ?: "") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
        Spacer(modifier = Modifier.height(24.dp))

        Text("Edit Profile", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            val userRepo = com.example.c36b.repository.UserRepositoryImpl()
            val userId = user.userId // fixed: use user.userId instead of user.id
            val data = mutableMapOf<String, Any?>()
            data["name"] = name
            data["bio"] = bio
            // add other fields as needed
            userRepo.editProfile(userId, data) { success, msg ->
                message = if (success) "Profile updated!" else "Error: $msg"
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Save Changes")
        }
        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, color = MaterialTheme.colorScheme.primary)
        }
    }
}
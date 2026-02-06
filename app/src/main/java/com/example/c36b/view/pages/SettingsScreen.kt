package com.example.c36b.view.pages

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.c36b.repository.UserRepositoryImpl
import android.content.Intent
import com.example.c36b.view.LoginActivity


@Composable
fun SettingsScreen() {
    var isDarkMode by remember { mutableStateOf(false) }
    var logoutMessage by remember { mutableStateOf("") }
    var deleteMessage by remember { mutableStateOf("") }
    val userRepo = UserRepositoryImpl()
    val context = LocalContext.current
    val activity = context as? Activity
    Surface(color = if (isDarkMode) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Settings", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Dark Mode", modifier = Modifier.weight(1f))
                Switch(checked = isDarkMode, onCheckedChange = { isDarkMode = it })
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                userRepo.logout { success, message ->
                    logoutMessage = message
                    if (success) {
                        // Handle successful logout, e.g., navigate to login screen
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        activity?.finish() // Close the current activity
                    } else {
                        // Handle logout failure
                        logoutMessage = message

                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Logout")
            }
            if (logoutMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(logoutMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(32.dp))
            // Add other settings options here
            val firebaseUser = userRepo.getCurrentUser()
            Button(onClick = {
                val userId = firebaseUser?.uid
                if (userId != null) {
                    userRepo.deleteAccount(userId) { success, msg ->
                        deleteMessage = msg
                        if (success) {
                            // Optionally log out and navigate to login
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                            activity?.finish()
                        }
                    }
                } else {
                    deleteMessage = "User not found."
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Delete Account")
            }
            if (deleteMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(deleteMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* TODO: Implement notification settings */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Notification Settings")
            }
        }
    }
}
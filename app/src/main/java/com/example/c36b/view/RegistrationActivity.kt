package com.example.c36b.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.example.c36b.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.c36b.model.UserModel
import com.example.c36b.repository.UserAuthRepositoryImpl
import com.example.c36b.repository.UserRepositoryImpl
import com.example.c36b.ui.theme.C36BTheme
import com.example.c36b.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.Black.toArgb()))
        setContent {
            registerBody()
        }
    }
}

@Composable
fun registerBody() {
    val repo = remember { UserRepositoryImpl() }
    val authRepo = remember { UserAuthRepositoryImpl(FirebaseAuth.getInstance()) }

    val userViewModel = remember { UserViewModel(repo,authRepo) }
    val context = LocalContext.current
    val activity = context as? Activity
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    Scaffold { innerPadding->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(painter = painterResource(R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize())
            LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.width(150.dp).height(150.dp)
                            .padding(0.dp, 20.dp, 0.dp, 0.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "News Fest",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(150.dp))
                }
                item {
                    Text(
                        "Create An Account",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 30.sp
                        ),
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        textStyle = TextStyle(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedPlaceholderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            cursorColor = Color.Red
                        ),

                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        placeholder = {
                            Text(text = "Name")
                        },
                        value = name,
                        onValueChange = { input ->
                            name = input
                        }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        textStyle = TextStyle(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedPlaceholderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            cursorColor = Color.Red
                        ),

                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        placeholder = {
                            Text(text = "Email")
                        },
                        value = email,
                        onValueChange = { input ->
                            email = input
                        }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedPlaceholderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            cursorColor = Color.Red
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        placeholder = {
                            Text(text = "Password")
                        },
                        value = password,
                        onValueChange = { input ->
                            password = input
                        }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    OutlinedButton(onClick = {
                        Log.d("ButtonClick", "Signup button clicked")

                        userViewModel.register(email, password) { success, message, userId ->
                            Log.d("RegisterCallback", "Register callback hit: success=$success")

                            if (success) {
                                val model = UserModel(userId, email, name,password)
                                Log.d("BeforeAddUser", "Calling addUserToDatabase with: $model")

                                userViewModel.addUserToDatabase(userId, model) { success, message ->
                                    Log.d("AddUserCallback", "Add user callback hit: success=$success")

                                    if (success) {
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                        val intent = Intent(context, LoginActivity::class.java)
                                        context.startActivity(intent)
                                        activity?.finish()
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                        modifier = Modifier.height(50.dp).width(200.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = "Signup")
                    }

                }
            }
        }

    }
}
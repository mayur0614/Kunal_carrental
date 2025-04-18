package com.example.carrentalapp

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(navController: NavHostController, padding: Modifier) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("normal_user") }
    var isRegistering by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (isRegistering) "Register" else "Login", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (isRegistering) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = userType == "normal_user",
                        onClick = { userType = "normal_user" }
                    )
                    Text("Normal User")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = userType == "car_owner",
                        onClick = { userType = "car_owner" }
                    )
                    Text("Car Owner")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true
                if (isRegistering) {
                    registerUser(email, password, userType, context, navController) {
                        isLoading = false
                    }
                } else {
                    loginUser(email, password, context, navController) {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text(if (isRegistering) "Register" else "Login")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isRegistering) "Already have an account? Login" else "Don't have an account? Register",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { isRegistering = !isRegistering }
        )
    }
}
fun registerUser(
    email: String,
    password: String,
    role: String,
    context: Context,
    navController: NavHostController,
    onDone: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                // ⚠️ WARNING: Don't store plain text passwords in production!
                val user = mapOf(
                    "email" to email,
                    "password" to password, // for demo only
                    "role" to role
                )

                db.collection("users").document(userId).set(user)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                        navController.navigate("login")
                        onDone()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to save user: ${it.message}", Toast.LENGTH_SHORT).show()
                        onDone()
                    }
            } else {
                Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                onDone()
            }
        }
}

fun loginUser(
    email: String,
    password: String,
    context: Context,
    navController: NavHostController,
    onDone: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                db.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        val storedEmail = document.getString("email")
                        val storedPassword = document.getString("password") // 🔐

                        if (storedEmail == email && storedPassword == password) {
                            val role = document.getString("role")
                            when (role) {
                                "car_owner" -> navController.navigate("main")
                                "normal_user" -> navController.navigate("userScreen")
                                else -> Toast.makeText(context, "No role assigned", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                        }
                        onDone()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                        onDone()
                    }
            } else {
                Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                onDone()
            }
        }
}


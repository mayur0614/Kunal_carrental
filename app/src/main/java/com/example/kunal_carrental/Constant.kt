package com.example.carrentalapp

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object Constant {
    var ownerName : String = "sagar"
    var userReq : Boolean = false
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

fun shareCarDetails(context: Context, car: Car) {
    val shareText = """
        🚗 *Car Details* 🚗
        📌 Name: ${car.name}
        👤 Owner: ${car.ownerName}
        💺 Seats: ${car.seats}
        ⛽ Fuel: ${car.fuelMode}
        💰 Rate: ₹${car.rate}/day
        📞 Contact: ${car.contactInfo}
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Car Details"))
}
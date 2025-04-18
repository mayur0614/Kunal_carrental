package com.example.carrentalapp.screenList


import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.AttachMoney

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.platform.LocalContext
import com.example.carrentalapp.Car
import com.example.carrentalapp.shareCarDetails


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun UserCarDetailScreen(navController: NavHostController, car: Car) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 🧭 Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Car Details",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B6CB7),
                    fontSize = 20.sp
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🚗 Car Image Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE5EAF0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CarRental,
                    contentDescription = "Car Image Placeholder",
                    modifier = Modifier.size(100.dp),
                    tint = Color(0xFF4B6CB7)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 📋 Car Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = car.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4B6CB7)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Owner: ${car.ownerName}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFF4B6CB7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Owner", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 🚦 Car Features
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    CarFeatureItem1(Icons.Default.LocalGasStation, "Fuel", car.fuelMode)
                    CarFeatureItem1(Icons.Default.EventSeat, "Seats", car.seats)
                    CarFeatureItem1(Icons.Default.AttachMoney, "Price", "₹${car.rate}/day")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ☎️ Call Owner Button
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${car.contactInfo}")
                }
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B6CB7))
        ) {
            Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Call Owner", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 📤 Share Button
        Button(
            onClick = { shareCarDetails(context, car) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E9EAB))
        ) {
            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share Car Details", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun CarFeatureItem1(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = Color(0xFF4B6CB7), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}
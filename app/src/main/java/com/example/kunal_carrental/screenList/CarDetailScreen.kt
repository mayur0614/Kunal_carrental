package com.example.carrentalapp.screenList

import android.content.Context
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

@Composable
fun CarDetailScreen(navController: NavHostController, car: Car) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(Color(0xFFF5F7FA)),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 🚘 Car Image Placeholder
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF4B6CB7).copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.CarRental,
                    contentDescription = "Car Image",
                    modifier = Modifier.size(100.dp),
                    tint = Color(0xFF4B6CB7)
                )
            }
        }

        // 📋 Car Information Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = car.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4B6CB7)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Owner: ${car.ownerName}",
                            fontSize = 14.sp,
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

                // 🚦 Features
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    CarFeatureItem(Icons.Default.LocalGasStation, "Fuel", car.fuelMode)
                    CarFeatureItem(Icons.Default.EventSeat, "Seats", car.seats)
                    CarFeatureItem(Icons.Default.AttachMoney, "Price", "₹${car.rate}/day")
                }
            }
        }

        // 📞 Contact + 📤 Share
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${car.contactInfo}")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B6CB7))
            ) {
                Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call Owner", color = Color.White)
            }

            Button(
                onClick = { shareCarDetails(context, car) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E9EAB))
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share", color = Color.White)
            }
        }
    }
}

@Composable
fun CarFeatureItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = Color(0xFF4B6CB7))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

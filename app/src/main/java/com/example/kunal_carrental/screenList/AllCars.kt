package com.example.carrentalapp.screenList

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.carrentalapp.Car
import com.example.carrentalapp.Constant
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCars(navController: NavHostController) {
    val cars = remember { mutableStateOf<List<Car>>(emptyList()) }
    var listenerRegistration: ListenerRegistration? = null

    LaunchedEffect(Unit) {


        listenerRegistration = db.collection("cars")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("OwnerCars", "listen failed", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val carList = snapshot.documents.map { document ->
                        Car(
                            id = document.id,
                            name = document.getString("carName") ?: "",
                            ownerName = document.getString("ownerName") ?: "",
                            seats = document.getString("seats") ?: "",
                            fuelMode = document.getString("fuelMode") ?: "",
                            contactInfo = document.getString("contactInfo") ?: "",
                            rate = document.getString("rate") ?: ""
                        )
                    }
                    cars.value = carList
                } else {
                    cars.value = emptyList() // No cars found
                }
            }
    }

    DisposableEffect(Unit) {
        onDispose {
            listenerRegistration?.remove()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        // Show message if no cars exist
        if (cars.value.isEmpty()) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()

            }
            //Text(text = "No cars found", fontSize = 18.sp, color = Color.Gray)
        } else {


            LazyVerticalGrid(
                columns = GridCells.Fixed(1), // 2 items per row
                contentPadding = PaddingValues(8.dp)
            ) {
                items(cars.value.reversed()) { car ->
                    CarCard1(
                        car,
                        navController = navController,
                        cars
                    )
                }
            }
        }
    }
}@Composable
fun CarCard1(car: Car, navController: NavHostController, cars: MutableState<List<Car>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f))
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            // 🚘 Header: Car icon + Info + Rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CarRental,
                    contentDescription = "Car Icon",
                    modifier = Modifier
                        .size(52.dp)
                        .padding(end = 12.dp),
                    tint = Color(0xFF4B6CB7)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = car.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B6CB7)
                    )
                    Text(
                        text = "Owner: ${car.ownerName}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "₹${car.rate}/day",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF182848)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🚙 Car features
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                FeatureItem(icon = Icons.Default.LocalGasStation, label = "Fuel", value = car.fuelMode)
                FeatureItem(icon = Icons.Default.EventSeat, label = "Seats", value = car.seats)
                FeatureItem(icon = Icons.Default.Phone, label = "Contact", value = car.contactInfo)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔘 View Details button
            Button(
                onClick = {
                    val carJson = Uri.encode(Gson().toJson(car))
                    if (!Constant.userReq) {
                        navController.navigate("carDetail/$carJson")
                    } else {
                        navController.navigate("UserCarDetail/$carJson")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B6CB7))
            ) {
                Icon(Icons.Default.Info, contentDescription = "View", modifier = Modifier.size(20.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Details", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
@Composable
fun FeatureItem(icon: ImageVector, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0x1A4B6CB7), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF4B6CB7)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
    }
}


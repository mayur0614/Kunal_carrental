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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.carrentalapp.Car
import com.example.carrentalapp.Constant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson

val db = FirebaseFirestore.getInstance()
var isPosting  = false


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerCars(navController: NavHostController) {
    val cars = remember { mutableStateOf<List<Car>>(emptyList()) }
    var listenerRegistration: ListenerRegistration? = null
    LaunchedEffect(navController.currentBackStackEntry) {
        isPosting = false
    }

    LaunchedEffect(Unit) {
        val username = Constant.ownerName
        if(username == null ) return@LaunchedEffect
        listenerRegistration =
            db.collection("cars")
                .whereEqualTo("ownerName",username)
                .addSnapshotListener{snapshot,e ->
                    if(e != null){
                        Log.w("homeScreen","Listen failed",e)
                        return@addSnapshotListener
                    }
                    if(snapshot!=null && !snapshot.isEmpty){
                        var carList = snapshot.documents.map { document ->
                            Car(
                                id = document.id,
                                name = document.getString("carName") ?:"",
                                ownerName = document.getString("ownerName") ?: "", // ✅ Change this
                                seats = document.getString("seats") ?: "",
                                fuelMode = document.getString("fuelMode") ?: "",
                                contactInfo = document.getString("contactInfo") ?: "",
                                rate = document.getString("rate") ?: ""
                            )

                        }
                        cars.value = carList
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
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
                        CarCard(
                            car,
                            navController = navController,
                        )
                    }
                }
            }
            }
            if (!isPosting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 20.dp, end = 20.dp), // Adjust padding as needed
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("addCarScreen")
                            isPosting = true
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .padding(16.dp)
                            .size(56.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "", tint = Color.White)
                    }
                }

            }
        }
    }
}

@Composable
fun CarCard(car: Car, navController: NavHostController) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DirectionsCar, contentDescription = "", modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = car.name, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text(text = "Seats: ${car.seats} | Fuel: ${car.fuelMode}", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Contact: ${car.contactInfo}", fontSize = 14.sp, color = Color.DarkGray)
            Text(text = "Rate: ₹${car.rate}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    val carJson = Uri.encode(Gson().toJson(car))
                    navController.navigate("carDetail/$carJson")
                }) {
                    Text("View Details")
                }

                Row {
                    IconButton(onClick = { navController.navigate("editCarScreen/${car.id}") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF4CAF50))
                    }
                    IconButton(onClick = { deleteCarFromFirestore(car.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
        }
    }

}

@Composable
fun editPost(navController: NavController,modifier: Modifier,car: Car)
{
    IconButton(
        onClick = {
            navController.navigate("editCarScreen/${car.id}")
        },
        modifier = modifier
    ) {
        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
    }
}

@Composable
fun deletePost(car: Car, modifier: Modifier)
{

    IconButton(
        onClick = {
            deleteCarFromFirestore(car.id)
        },
        modifier = modifier
    ) {
        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Black)
    }
}
fun deleteCarFromFirestore(carId: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("cars").document(carId)
        .delete()
        .addOnSuccessListener {
            Log.d("DeleteCar", "Car successfully deleted!")
        }
        .addOnFailureListener { e ->
            Log.e("DeleteCar", "Error deleting car", e)
        }
}



@Composable
fun AddCarScreen(navController: NavHostController) {
    var carName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("") }
    var fuelMode by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }



    // UI for adding car details
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add Car Details", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))

        val textFieldModifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)

        OutlinedTextField(
            value = carName,
            onValueChange = { carName = it },
            label = { Text("Car Name") },
            leadingIcon = { Icon(Icons.Filled.DirectionsCar, contentDescription = "Car") },
            modifier = textFieldModifier
        )

        OutlinedTextField(
            value = seats,
            onValueChange = { seats = it },
            label = { Text("Seats") },
            leadingIcon = { Icon(Icons.Filled.EventSeat, contentDescription = "Seats") },
            modifier = textFieldModifier
        )

        OutlinedTextField(
            value = fuelMode,
            onValueChange = { fuelMode = it },
            label = { Text("Fuel Mode") },
            leadingIcon = { Icon(Icons.Filled.LocalGasStation, contentDescription = "Fuel") },
            modifier = textFieldModifier
        )

        OutlinedTextField(
            value = contactInfo,
            onValueChange = {
                contactInfo = it
                isValid = validateIndianPhoneNumber(it)
            },
            label = { Text("Phone Number") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
            isError = !isValid,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            modifier = textFieldModifier
        )

        OutlinedTextField(
            value = rate,
            onValueChange = { rate = it },
            label = { Text("Rate") },
            leadingIcon = { Icon(Icons.Filled.CurrencyRupee, contentDescription = "Rate") },
            modifier = textFieldModifier
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val carDetails = hashMapOf(
                    "carName" to carName,
                    "ownerName" to Constant.ownerName,
                    "seats" to seats,
                    "fuelMode" to fuelMode,
                    "contactInfo" to contactInfo,
                    "rate" to rate,
                )
                db.collection("cars").add(carDetails)
                    .addOnSuccessListener { navController.popBackStack() }
                    .addOnFailureListener { e -> Log.e("AddCarScreen", "Error adding car", e) }
            },
            enabled = isValid,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Post Car")
        }
    }

}




package com.example.carrentalapp.screenList

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.carrentalapp.Car
import com.example.carrentalapp.Constant
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val carRental = remember { mutableStateOf(true) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFB8C6DB), Color(0xFFF5F7FA))
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(carRental, navController, drawerState, scope)
        },
        modifier = Modifier.background(backgroundGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        AnimatedContent(targetState = carRental.value, label = "") { isRental ->
                            Text(
                                if (isRental) "Car Rental" else "Your Cars",
                                color = Color(0xFF4B6CB7),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color(0xFF4B6CB7)
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundGradient)
            ) {
                Constant.userReq = false
                NavigationHost(navController)
            }
        }
    }
}

@Composable
fun DrawerContent(
    carRental: MutableState<Boolean>,
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier
            .fillMaxHeight()
            .background(Color.White)
            .padding(vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // 🧭 Menu Title
            Text(
                text = "Menu",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B6CB7),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            )

            Divider(color = Color(0xFF4B6CB7).copy(alpha = 0.3f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))

            // 🚗 All Cars
            CustomDrawerItem(
                icon = Icons.Filled.DirectionsCar,
                label = "All Cars",
                selected = carRental.value,
                onClick = {
                    carRental.value = true
                    navController.navigate("allCars") {
                        popUpTo("allCars") { inclusive = true }
                    }
                    scope.launch { drawerState.close() }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 👨‍🔧 Owner Cars
            CustomDrawerItem(
                icon = Icons.Default.AccountBox,
                label = "Owner Cars",
                selected = !carRental.value,
                onClick = {
                    carRental.value = false
                    navController.navigate("ownerCars") {
                        popUpTo("ownerCars") { inclusive = true }
                    }
                    scope.launch { drawerState.close() }
                }
            )
        }
    }
}

@Composable
fun CustomDrawerItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) Color(0xFF4B6CB7).copy(alpha = 0.1f) else Color.Transparent
    val contentColor = if (selected) Color(0xFF4B6CB7) else Color.DarkGray

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp)
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(24.dp)
            )
            Text(
                text = label,
                color = contentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = "allCars") {
        composable("ownerCars") { OwnerCarsScreen(navController) }
        composable("allCars") { AllCarsScreen(navController) }
        composable("addCarScreen") { AddCarScreen(navController) }

        composable(
            route = "carDetail/{carJson}",
            arguments = listOf(navArgument("carJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val carJson = backStackEntry.arguments?.getString("carJson") ?: ""
            val car = Gson().fromJson(carJson, Car::class.java)
            CarDetailScreen(navController, car)
        }

        composable(
            route = "userCarDetail/{carJson}",
            arguments = listOf(navArgument("carJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val carJson = backStackEntry.arguments?.getString("carJson") ?: ""
            val car = Gson().fromJson(carJson, Car::class.java)
            UserCarDetailScreen(navController, car)
        }

        composable("editCarScreen/{carId}") { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            EditCarScreen(navController, carId)
        }

        composable("userScreen") {
            UserScreen(navController)
        }
    }
}



@Composable
fun OwnerCarsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OwnerCars(navController)
    }
}

@Composable
fun AllCarsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Center
    ) {
        AllCars(navController)
    }
}

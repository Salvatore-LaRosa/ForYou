package com.example.foryou.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foryou.R
import com.example.foryou.ui.theme.BackgroundColor
import com.example.foryou.ui.theme.MainColor
import com.example.foryou.viewmodel.AuthViewModel

/**
 * A composable function that displays the settings screen.
 * It currently provides an option to log out.
 *
 * @param navController The NavController for handling navigation.
 * @param authViewModel The AuthViewModel for handling authentication logic, like logout.
 */
@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // Main container for the settings screen with a background color and padding.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // Main column for laying out setting items vertically.
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Title of the settings screen.
            Text(
                text = "Impostazioni",
                color = MainColor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // System settings section.
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Title for the system section.
                Text(
                    text = "Sistema",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Clickable card for the logout functionality.
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable {
                            // When clicked, log out the user and navigate to the login screen.
                            authViewModel.logout()
                            navController.navigate("login") {
                                // Clears the entire navigation back stack.
                                popUpTo(0)
                            }
                        },
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    // Row to layout the text and icon inside the card.
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // "Esci" (Logout) text.
                        Text(
                            text = "Esci",
                            color = Color(0xFFFF3B30), // A red color to indicate a destructive action.
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        // Logout icon.
                        Image(
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = "Logout",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

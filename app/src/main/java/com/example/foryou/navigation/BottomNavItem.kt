package com.example.foryou.navigation

import androidx.annotation.DrawableRes
import com.example.foryou.R

sealed class BottomNavItem(
    val route: String,
    val label: String,
    @param:DrawableRes val icon: Int
) {
    object Home : BottomNavItem("main", "Home", R.drawable.ic_home)
    object Report : BottomNavItem("report", "Resoconto", R.drawable.ic_report)
    object Profile : BottomNavItem("profile", "Profilo", R.drawable.ic_profile)
    object Settings : BottomNavItem("settings", "Impostazioni", R.drawable.ic_settings)
}

package com.example.foryou.navigation

import androidx.annotation.DrawableRes
import com.example.foryou.R

/**
 * A sealed class that defines the items for the bottom navigation bar.
 * Each object represents a screen that can be accessed from the bottom navigation.
 *
 * @property route The navigation route for the screen.
 * @property label The text label to display for the item.
 * @property icon The drawable resource ID for the icon.
 */
sealed class BottomNavItem(
    val route: String,
    val label: String,
    @param:DrawableRes val icon: Int
) {
    /**
     * Represents the Home screen item.
     */
    object Home : BottomNavItem("main", "Home", R.drawable.ic_home)

    /**
     * Represents the Report screen item.
     */
    object Report : BottomNavItem("report", "Report", R.drawable.ic_report)

    /**
     * Represents the Settings screen item.
     */
    object Settings : BottomNavItem("settings", "Settings", R.drawable.ic_settings)
}

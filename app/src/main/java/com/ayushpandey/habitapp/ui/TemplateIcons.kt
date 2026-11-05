package com.ayushpandey.habitapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PhonelinkErase
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.ui.graphics.vector.ImageVector

fun templateIcon(key: String): ImageVector = when (key) {
    "water" -> Icons.Outlined.WaterDrop
    "sun" -> Icons.Outlined.WbSunny
    "walk" -> Icons.Outlined.DirectionsWalk
    "stretch" -> Icons.Outlined.AccessibilityNew
    "fitness" -> Icons.Outlined.FitnessCenter
    "meditate" -> Icons.Outlined.SelfImprovement
    "journal" -> Icons.Outlined.MenuBook
    "favorite" -> Icons.Outlined.FavoriteBorder
    "book" -> Icons.Outlined.AutoStories
    "focus" -> Icons.Outlined.Psychology
    "school" -> Icons.Outlined.School
    "phoneless" -> Icons.Outlined.PhonelinkErase
    "restaurant" -> Icons.Outlined.Restaurant
    "eco" -> Icons.Outlined.Eco
    "bed" -> Icons.Outlined.Bedtime
    "home" -> Icons.Outlined.Home
    "chat" -> Icons.Outlined.ChatBubbleOutline
    "payments" -> Icons.Outlined.Payments
    "spa" -> Icons.Outlined.Spa
    else -> Icons.Outlined.LocalCafe
}

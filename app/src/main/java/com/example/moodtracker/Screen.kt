package com.example.moodtracker



import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.moodtracker.ui.theme.*
import java.time.LocalDateTime

//Main Data
val moods = listOf("Happy", "Content", "Okay", "Moody", "Angry", "None")
val colors = listOf(green, lightgreen, white, lightyellow, red, gray)
val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
val todayInfo = createToday()


//Navigation
sealed class Screen(val rout: String) {
    data object Home : Screen("Home")
    data object Mood : Screen("Mood")
    data object Stat : Screen("Stat")
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val listOfBottomNavItems = listOf(
    BottomNavItem("Home", icon = Icons.Default.Home, route = Screen.Home.rout),
    BottomNavItem("Mood", icon = Icons.Default.Create, route = Screen.Mood.rout),
    BottomNavItem("Stats", icon = Icons.Default.Info, route = Screen.Stat.rout)

)

data class Today(
    val month: Int,
    val day: Int
)



private fun createToday(): Today {
    val localInfo = LocalDateTime.now()
    //monthValue is decrement by 1 since months list is 0 indexed
    return Today(localInfo.monthValue-1, localInfo.dayOfMonth)
}



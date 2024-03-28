package com.example.moodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moodtracker.ui.theme.MOODTRACKERTheme
import com.example.moodtracker.ui.theme.gray
import com.example.moodtracker.ui.theme.peach

@Suppress("UNCHECKED_CAST")
class MainActivity : ComponentActivity() {
    private val db by lazy {
        EntryDataBase.getDatabase(context = applicationContext)
    }

    private val viewModel by viewModels<TestViewModel>( factoryProducer = {object: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TestViewModel(db.dao) as T
        }
    }})
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //viewModel.debugDeleteAllRecords()
            //viewModel.debugInitDatabase()
            viewModel.getMonthRecords(todayInfo.month)
            println("Running")
            MOODTRACKERTheme {
                Navigation(viewModel)
            }
        }
    }
}

@Composable
fun Navigation (viewModel: TestViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar (
                modifier = Modifier.drawBehind {
                    drawLine(
                        color = gray,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 2.dp.toPx()
                    )
                            },
                containerColor = MaterialTheme.colorScheme.background) {
                val navBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
                val currentDestination: NavDestination? = navBackStackEntry?.destination

                listOfBottomNavItems.forEach { navItem ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any{ it.route == navItem.route} == true,
                        onClick = { if (currentDestination?.route != navItem.route){navController.navigate(navItem.route)} },
                        icon = { Icon(imageVector = navItem.icon, contentDescription = null) },
                        label = { Text(navItem.label)},
                        colors = NavigationBarItemColors(
                            selectedIconColor = gray,
                            selectedTextColor = gray,
                            selectedIndicatorColor = peach,
                            unselectedIconColor = gray,
                            unselectedTextColor = gray,
                            disabledIconColor = gray,
                            disabledTextColor = gray)
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Mood.rout,
            modifier = Modifier.padding(paddingValues)
        ) {

            //Home
            composable(Screen.Home.rout)
            {
                HomeScreen(viewModel)
            }

            //Mood
            composable(Screen.Mood.rout)
            {
                MoodScreen(navController, viewModel)
            }

            composable(Screen.Stat.rout)
            {
                viewModel.getMood()
                StatScreen(viewModel)
            }
        }
    }


}
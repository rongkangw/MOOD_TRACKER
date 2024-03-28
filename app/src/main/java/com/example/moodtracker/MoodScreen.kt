package com.example.moodtracker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlin.random.Random

@Composable
fun MoodScreen(
    navController: NavHostController,
    viewModel: TestViewModel
) {
    var index by remember { mutableIntStateOf(0) }
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        )
        {
            Text(
                text = "How was your day?",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(50.dp))
        Box(modifier = Modifier.height(280.dp)) {
            Box(modifier = Modifier
                .height(300.dp)
                .width(300.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(colors[index])
                .clickable {
                    index += 1
                    if (index > 4) {
                        index = 0
                    }
                }
            )
            {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
                {
                    Text(text = moods[index], fontSize = 30.sp)
                }

            }
        }
        Spacer(Modifier.height(50.dp))
        Button(
            onClick = {
                viewModel.state.value.mood = moods[index]
                viewModel.state.value.month = Random.nextInt(1, todayInfo.month)
                viewModel.state.value.day =  Random.nextInt(1, todayInfo.day) //todayInfo.day
                viewModel.insertEntry()
                navController.navigate(Screen.Home.rout)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Submit",
                color = MaterialTheme.colorScheme.background,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center)
        }
    }

}



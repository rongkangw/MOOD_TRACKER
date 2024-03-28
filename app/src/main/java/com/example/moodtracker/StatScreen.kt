package com.example.moodtracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moodtracker.ui.theme.lightgray

@Composable
fun StatScreen(viewModel: TestViewModel){
    val moodCountState = viewModel.countState.collectAsStateWithLifecycle()
    val happyCount = remember { moodCountState.value.happyCount }
    val contentCount = remember { moodCountState.value.contentCount }
    val okayCount = remember { moodCountState.value.okayCount }
    val angryCount = remember { moodCountState.value.angryCount }
    val moodyCount = remember { moodCountState.value.moodyCount }
    Column (modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp))
    {
        Text(text = "Total by Mood",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)

        Card (modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.16f),
            colors = CardDefaults.cardColors(containerColor = lightgray),) {
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly){
                MoodStatColumn(0, happyCount)
                MoodStatColumn(1, contentCount)
                MoodStatColumn(2, okayCount)
                MoodStatColumn(3, moodyCount)
                MoodStatColumn(4, angryCount)
            }
        }
        Spacer(Modifier.height(300.dp))
        Button(onClick = {
            viewModel.debugDeleteAllRecords()
            viewModel.debugInitDatabase()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ){
          Text(text = "Clear All Data",
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.background
          )
        }
    }
}

@Composable
fun MoodStatColumn (moodColor: Int, count: Int){
    Column (modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Box(modifier = Modifier
            .size(50.dp)
            .padding(5.dp)
            .clip(shape = RoundedCornerShape(13.dp))
            .background(color = colors[moodColor]),
            contentAlignment = Alignment.Center)
        {
            Text(
                text = count.toString(),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.background)
        }
        Text(text = moods[moodColor],
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.background,
            textAlign = TextAlign.Center)
    }
}
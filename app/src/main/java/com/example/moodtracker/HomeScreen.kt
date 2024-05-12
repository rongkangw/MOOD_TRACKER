package com.example.moodtracker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.moodtracker.ui.theme.*

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: TestViewModel
) {
    /*
    using 'by' requires
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.setValue
    */
    var clickedDay by remember { mutableStateOf<EntryDay?>(null) }
    val currentMonth = viewModel.currentMonth.collectAsStateWithLifecycle().value

    Column (modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 15.dp, vertical = 45.dp)) {

        Row (modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {

            IconButton(onClick = {
                viewModel.decrementMonth()
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, tint = MaterialTheme.colorScheme.primary, contentDescription = null)
            }

            Text(
                modifier = Modifier.fillMaxWidth(0.70f),
                textAlign = TextAlign.Center,
                text = months[currentMonth],
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 40.sp,
            )

            IconButton(onClick = {
                viewModel.incrementMonth()
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, tint = MaterialTheme.colorScheme.primary, contentDescription = null)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                MoodCalendarV2(
                    onDayClick = { day -> clickedDay = day},
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.47f))
                Column (
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .align(Alignment.CenterHorizontally)
                        .height(230.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(lightgray.copy(alpha = 0.2f))
                        .padding(15.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp))
                {
                    clickedDay?.let {
                        Text(text = "Day : " + it.day.toString(),
                            color = white,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 25.sp
                        )

                        Text(text = moods[it.mood],
                            color = white,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 25.sp
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        Box(modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = {
                                    viewModel.setState(it.month, it.day)
                                    navController.navigate(Screen.Mood.rout)

                                }
                            ) {
                                Text("Change Record", textAlign = TextAlign.Center)
                            }
                        }

                    }
                }
            }

        }
    }
}

@Composable
fun MoodCalendarV2(
    modifier: Modifier = Modifier,
    onDayClick: (EntryDay) -> Unit,
    viewModel: TestViewModel
) {
    val recordList = viewModel.outputState.collectAsStateWithLifecycle().value.recordList
    LazyVerticalGrid(modifier = modifier,
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(15.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp))
    {
        items(recordList){day ->
            DayCard(mood = day.mood,
                month = day.month,
                day = day.day,
                onDayClick = onDayClick,
                viewModel = viewModel,
                recordList = recordList
            )
        }

    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DayCard (mood : Int, month: Int, day : Int, onDayClick: (EntryDay) -> Unit, viewModel: TestViewModel, recordList: List<EntryDay>) {
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value){
        DeletePopup(viewModel = viewModel,
            month = month,
            day = day,
            showDialog = showDialog.value,
            onDismiss = { showDialog.value = false }
        )
    }
    Card(
        modifier = Modifier
            .fillMaxSize()
            .height(50.dp)
            .combinedClickable(
                onClick = { onDayClick(recordList[day - 1]) },
                onLongClick = {
                    if (mood != 0) {
                        showDialog.value = true
                    }
                }
            ),
        shape = RoundedCornerShape(13.dp),
        border = BorderStroke(1.dp, gray),
        colors = CardDefaults.cardColors(
            containerColor =
            if (mood == 0){
                lightgray.copy(alpha = 0.2f)
            }
            else{
                colors[mood]
            }
        )
    ) {
        Text(
            modifier = Modifier.offset(x = 8.dp, y = 3.dp),
            text = day.toString(),
            textAlign = TextAlign.Left,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
    }
}

@Composable
fun DeletePopup(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    viewModel: TestViewModel,
    month: Int,
    day: Int
){
    if (showDialog){
        AlertDialog(
            icon = {Icons.Default.Info},
            title = { Text("Delete?")},
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEntry(month, day)
                        println("Entry($month, $day) deleted.")
                        onDismiss()
                    },

                ) {
                    Text("Confirm", textAlign = TextAlign.Right)
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Dismiss", textAlign = TextAlign.Left)
                }

            }
        )
    }
    
}
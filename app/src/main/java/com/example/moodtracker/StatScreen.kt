package com.example.moodtracker

import android.graphics.PorterDuff
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moodtracker.ui.theme.green
import com.example.moodtracker.ui.theme.lightgray
import com.example.moodtracker.ui.theme.red
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.zoom.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.shape.dashedShape
import com.patrykandpatrick.vico.compose.component.shape.shader.fromComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.shape.shader.TopBottomShader
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.model.lineSeries
import com.patrykandpatrick.vico.core.zoom.Zoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun StatScreen(viewModel: TestViewModel){

    val moodCountState = viewModel.countState.collectAsStateWithLifecycle()

    Column (modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(25.dp))
    {

        Text(text = "Highlights",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        
        MoodCount(moodCountState.value)
    
        MoodChart(moodCountState.value.moodChartList)

        Spacer(Modifier.height(280.dp))

        ClearDataButton(viewModel)

    }
}


@Composable
fun ClearDataButton(viewModel : TestViewModel){
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value){
        ClearPopup(viewModel = viewModel,
            showDialog = showDialog.value,
            onDismiss = { showDialog.value = false }
        )
    }

    Button(onClick = {
        showDialog.value = true
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
@Composable
fun ClearPopup(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    viewModel: TestViewModel
){
    if (showDialog){
        AlertDialog(
            icon = { Icons.Default.Info},
            title = { Text("Reset ALL Data?")},
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.debugDeleteAllRecords()
                        viewModel.debugInitDatabase()
                        onDismiss()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Dismiss")
                }

            }
        )
    }

}



@Composable
fun MoodCount(moodCountState: CountState){
    val happyCount = remember { moodCountState.happyCount }
    val contentCount = remember { moodCountState.contentCount }
    val okayCount = remember { moodCountState.okayCount }
    val angryCount = remember { moodCountState.angryCount }
    val moodyCount = remember { moodCountState.moodyCount }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "Total by Mood",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary)

        Card (modifier = Modifier
            .fillMaxHeight(0.13f),
            colors = CardDefaults.cardColors(containerColor = lightgray.copy(alpha = 0.2f)),) {
            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly){
                MoodCountStat(1, happyCount)
                MoodCountStat(2, contentCount)
                MoodCountStat(3, okayCount)
                MoodCountStat(4, moodyCount)
                MoodCountStat(5, angryCount)
            }
        }
    }

}


@Composable
fun MoodCountStat (moodColor: Int, count: Int){
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
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun NoDataScreen(){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Text(text = "No Data :(",
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }

}

@Composable
fun MoodChart(monthItem: List<ChartMonthItem>){

    val modelProducer = remember { CartesianChartModelProducer.build() }

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Average Mood for this Month (${months[todayInfo.month]})",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        val x = (1..monthItem[todayInfo.month].yValues.size).toList()
        LaunchedEffect(Unit) {
            withContext(Dispatchers.Default) {
                modelProducer.tryRunTransaction {
                    lineSeries {
                        series(
                            x = x,
                            y = (monthItem[todayInfo.month].yValues).map{if(it != 0) it-3 else 0}
                        )
                    }
                }
            }
        }
        //println(monthItem.size)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f),
            colors = CardDefaults.cardColors(containerColor = lightgray.copy(alpha = 0.2f))
        ) {
            if(monthItem[todayInfo.month].yValues.containsAll(listOf(0))){
                NoDataScreen()
            }
            else{
                MoodMonthChart(modelProducer)
            }

        }


    }



}


@Composable
fun MoodMonthChart(modelProducer: CartesianChartModelProducer){

    //val marker = rememberMarkerComponent(labelFormatter =  )
    val zoomState = rememberVicoZoomState(initialZoom = Zoom.Content)
    CartesianChartHost(
        modifier = Modifier.padding(15.dp),
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lines = listOf(
                    rememberLineSpec(
                        shader =
                        DynamicShaders.verticalGradient(
                            colors.subList(0,5).toTypedArray().reversedArray(), null
                        ),
                        backgroundShader =
                        TopBottomShader(
                            DynamicShaders.composeShader(
                                DynamicShaders.fromComponent(
                                    componentSize = 4.dp,
                                    component =
                                    rememberShapeComponent(
                                        shape = Shapes.rectShape,
                                        color = green.copy(alpha = 0.6f),
                                        margins = remember { dimensionsOf(1.dp) },
                                    ),
                                    checkeredArrangement = false,
                                ),
                                DynamicShaders.verticalGradient(
                                    arrayOf(Color.Black, Color.Transparent),
                                ),
                                PorterDuff.Mode.DST_IN,
                            ),

                            DynamicShaders.composeShader(
                                DynamicShaders.fromComponent(
                                    componentSize = 4.dp,
                                    component =
                                    rememberShapeComponent(
                                        shape = Shapes.rectShape,
                                        color = red.copy(alpha = 0.6f),
                                        margins = remember { dimensionsOf(1.dp) },
                                    ),
                                    checkeredArrangement = false,
                                ),
                                DynamicShaders.verticalGradient(
                                    arrayOf(Color.Transparent, Color.Black),
                                ),
                                PorterDuff.Mode.DST_IN,
                            ),
                        ),
                    ),
                ),
            ),
            startAxis = rememberStartAxis(
                label = rememberAxisLabelComponent(
                    color = MaterialTheme.colorScheme.primary,
                    background = null,
                    padding = remember { dimensionsOf(horizontal = 8.dp) }
                ),
                axis = null,
                tick = null,
                guideline = rememberLineComponent(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = remember {
                        Shapes.dashedShape(
                            shape = Shapes.pillShape,
                            dashLength = 4.dp,
                            gapLength = 8.dp,
                        )
                    }
                ),
                itemPlacer = remember { AxisItemPlacer.Vertical.count({0}) }
            ),
            bottomAxis = rememberBottomAxis(
                guideline = null,
                itemPlacer = remember {
                    AxisItemPlacer.Horizontal.default(spacing = 1, addExtremeLabelPadding = true)
                }
            )
        ),
        modelProducer = modelProducer,
        runInitialAnimation = true,
        horizontalLayout = HorizontalLayout.FullWidth(),
        zoomState = zoomState
    )
}
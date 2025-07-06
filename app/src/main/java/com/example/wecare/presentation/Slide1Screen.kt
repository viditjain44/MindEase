package com.example.wecare.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment as UiAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

@Composable
fun Slide1Screen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var resultText by remember { mutableStateOf("Fetching prediction...") }
    var isLoading by remember { mutableStateOf(true) }

    val swipeState = rememberSwipeToDismissBoxState()

    // Handle swipe to go back
    LaunchedEffect(swipeState.currentValue) {
        if (swipeState.currentValue == SwipeToDismissValue.Dismissed) {
            navController.popBackStack()
        }
    }

    // Fetch API on load
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = fetchSlide1Prediction()
                resultText = response.message
            } catch (e: Exception) {
                resultText = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    SwipeToDismissBox(state = swipeState) {
        Scaffold(
            timeText = { TimeText() }
        ) {
            ScalingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE3F2FD)) // Light blue background
                    .padding(8.dp),
                horizontalAlignment = UiAlignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Based on your current vitals",
                        color = Color.DarkGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))

                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        val isFit = resultText.contains("fit", ignoreCase = true) &&
                                !resultText.contains("not", ignoreCase = true)

                        val infiniteTransition = rememberInfiniteTransition()
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = if (!isFit) 1.2f else 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            )
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            onClick = {}
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(Color(0xFFBBDEFB), shape = MaterialTheme.shapes.medium)
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = UiAlignment.CenterHorizontally
                            ) {
                                Text(
                                    text = resultText,
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                AnimatedVisibility(visible = true) {
                                    if (isFit) {
                                        Text(
                                            text = "🎆🎇✨",
                                            fontSize = 24.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    } else {
                                        Text(
                                            text = "👎",
                                            fontSize = 28.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.scale(scale)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(.05.dp))
                    Button(
                        onClick = { navController.navigate("slide2") },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(top = 1.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF43A047), // Success Green
                            contentColor = Color.White
                        )
                    ) {
                        Text("Continue →", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}


suspend fun fetchSlide1Prediction(): Slide1Response = withContext(Dispatchers.IO) {
    val url = URL("https://stopwatch-measure.onrender.com/predict")

    val params = JSONObject().apply {
        put("heart_rate", Random.nextDouble(70.0, 90.0))
        put("blood_oxygen", Random.nextDouble(96.0, 100.0))
        put("steps", Random.nextInt(3000, 12000))
        put("sleep_duration", Random.nextDouble(3.5, 9.0))
        put("activity_level", Random.nextInt(0, 2))
    }

    val connection = (url.openConnection() as HttpURLConnection).apply {
        requestMethod = "POST"
        doOutput = true
        setRequestProperty("Content-Type", "application/json")
        outputStream.write(params.toString().toByteArray())
    }

    val responseText = connection.inputStream.bufferedReader().readText()
    val json = JSONObject(responseText)

    return@withContext Slide1Response(
        status = json.getString("status"),
        message = json.getJSONObject("prediction details").getString("message")
    )
}

data class Slide1Response(
    val status: String,
    val message: String
)

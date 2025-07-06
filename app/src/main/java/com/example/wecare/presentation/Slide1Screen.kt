package com.example.wecare.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment as UiAlignment
import androidx.compose.ui.Modifier
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

    SwipeToDismissBox(
        state = swipeState,

    ) {
        Scaffold(
            timeText = { TimeText() }
        ) {
            ScalingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = UiAlignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    if (isLoading) {
                        Text("Loading...", color = Color.Black)
                    } else {
                        Text(
                            text = resultText,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { navController.navigate("slide2") },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(0.6f)
                    ) {
                        Text("→", fontSize = 20.sp, color = Color.Black)
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

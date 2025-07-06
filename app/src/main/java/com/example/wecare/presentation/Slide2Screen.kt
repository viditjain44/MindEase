package com.example.wecare.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment as UiAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
fun Slide2Screen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var resultText by remember { mutableStateOf("Fetching prediction...") }
    var isLoading by remember { mutableStateOf(true) }

    val dismissState = rememberSwipeToDismissBoxState()

    // Swipe-to-go-back support
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissValue.Dismissed) {
            navController.popBackStack()
        }
    }

    // Fetch API
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = fetchFitnessPredictionSlide2()
                resultText = "${response.message}\nConfidence: ${response.confidence}%"
            } catch (e: Exception) {
                resultText = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    SwipeToDismissBox(state = dismissState) {
        Scaffold(timeText = { TimeText() }) {
            ScalingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 12.dp),
                horizontalAlignment = UiAlignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    if (isLoading) {
                        Text("Loading...", color = Color.Black, textAlign = TextAlign.Center)
                    } else {
                        Text(
                            resultText,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = { navController.popBackStack() }) {
                                Text("←", color = Color.Black)
                            }
                            Button(onClick = { navController.navigate("slide3") }) {
                                Text("→", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

suspend fun fetchFitnessPredictionSlide2(): Slide2Response = withContext(Dispatchers.IO) {
    val url = URL("https://fitness-measure-6df8.onrender.com")

    val params = JSONObject().apply {
        put("bmi", Random.nextDouble(15.0, 30.0))
        put("body_fat", Random.nextDouble(10.0, 30.0))
        put("broad_jump", Random.nextDouble(1.5, 2.5))
        put("sit_and_bend_forward", Random.nextDouble(10.0, 25.0))
        put("sit_ups_counts", Random.nextInt(10, 60))
    }

    val connection = (url.openConnection() as HttpURLConnection).apply {
        requestMethod = "POST"
        doOutput = true
        setRequestProperty("Content-Type", "application/json")
        outputStream.write(params.toString().toByteArray())
    }

    val responseText = connection.inputStream.bufferedReader().readText()
    val json = JSONObject(responseText)

    return@withContext Slide2Response(
        status = json.getString("status"),
        message = json.getString("message"),
        prediction_value = json.getInt("prediction_value"),
        confidence = json.getDouble("confidence")
    )
}

data class Slide2Response(
    val status: String,
    val message: String,
    val prediction_value: Int,
    val confidence: Double
)

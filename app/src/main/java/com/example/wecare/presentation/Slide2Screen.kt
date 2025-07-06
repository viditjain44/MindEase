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

    // Go back on swipe
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissValue.Dismissed) {
            navController.popBackStack()
        }
    }

    // API Call
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = fetchFitnessPredictionSlide2()
                resultText = "${response.message}\nConfidence: ${response.confidence}"
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
                    .padding(12.dp),
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
                            modifier = Modifier.padding(8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Navigate to slide3
                    }
                }
            }
        }
    }
}

suspend fun fetchFitnessPredictionSlide2(): Slide2Response = withContext(Dispatchers.IO) {
    val url = URL("https://fitness-measure-6df8.onrender.com/predict")

    val params = JSONObject().apply {
        put("bmi", Random.nextInt(18, 28))
        put("body_fat", Random.nextInt(5, 20))
        put("broad_jump", Random.nextInt(150, 250))
        put("sit_and_bend_forward", Random.nextInt(0, 20))
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
        is_fit = json.getBoolean("is_fit"),
        message = json.getString("message"),
        prediction_value = json.getInt("prediction_value"),
        confidence = json.getString("confidence")
    )
}

data class Slide2Response(
    val is_fit: Boolean,
    val message: String,
    val prediction_value: Int,
    val confidence: String
)

/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

//package com.example.wecare.presentation
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.wear.compose.material.MaterialTheme
//import androidx.wear.compose.material.Text
//import androidx.wear.compose.material.TimeText
//import androidx.wear.tooling.preview.devices.WearDevices
//import com.example.wecare.R
//import com.example.wecare.presentation.theme.WeCareTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()
//
//        super.onCreate(savedInstanceState)
//
//        setTheme(android.R.style.Theme_DeviceDefault)
//
//        setContent {
//            WearApp("Android")
//        }
//    }
//}
//
//@Composable
//fun WearApp(greetingName: String) {
//    WeCareTheme {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colors.background),
//            contentAlignment = Alignment.Center
//
//        ) {
//            TimeText()
//            Greeting(greetingName = greetingName)
//        }
//    }
//}
//
//@Composable
//fun Greeting(greetingName: String) {
//    Text(
//        modifier = Modifier.fillMaxWidth(),
//        textAlign = TextAlign.Center,
//        color = MaterialTheme.colors.primary,
//        text = stringResource(R.string.hello_world, greetingName)
//    )
//}
//
//@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    WearApp("Preview Android")
//}
package com.example.wecare.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment as UiAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.wear.compose.material.*

import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeCareApp()
        }
    }
}

@Composable
fun WeCareApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen(navController) }
        composable("slide1") { Slide1Screen(navController) }
        composable("slide2") { Slide2Screen(navController) }
        composable("result") { ResultScreen(navController) }
    }
}

@Composable
fun RandomMetricsScreen() {
    var heartRate by remember { mutableStateOf<Int?>(null) }
    var bodyTemp by remember { mutableStateOf<Float?>(null) }
    var sitUps by remember { mutableStateOf<Int?>(null) }
    var breathRate by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        timeText = { TimeText() }
    ) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = UiAlignment.CenterHorizontally
        ) {
            item {
                Button(
                    onClick = {
                        Log.d("RandomMetricsScreen", "Button Clicked")
                        heartRate = Random.nextInt(60, 101)
                        bodyTemp = Random.nextDouble(36.5, 38.5).toFloat()
                        sitUps = Random.nextInt(10, 51)
                        breathRate = Random.nextInt(12, 26)
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Get Stats", color = Color.Black)
                }
            }

            if (heartRate != null) {
                item { Text("Heart Rate: $heartRate bpm", color = Color.Black) }
                item { Text("Body Temp: ${String.format("%.1f", bodyTemp)} °C", color = Color.Black) }
                item { Text("Sit-Ups: $sitUps", color = Color.Black) }
                item { Text("Breaths: $breathRate", color = Color.Black) }
            } else {
                item {
                    Text("No Data Yet", textAlign = TextAlign.Center, color = Color.Black)
                }
            }
        }
    }
}


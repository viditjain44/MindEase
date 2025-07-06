package com.example.wecare.presentation

data class FitnessRequest(
    val heart_rate: Double,
    val blood_oxygen: Double,
    val steps: Int,
    val sleep_duration: Double,
    val activity_level: Int
)
//
//data class FitnessResponse(
//    val status: String,
//    val model_details: ModelDetails,
//    val prediction_details: PredictionDetails,
//    val input_details: Map<String, Any>
//)

//data class ModelDetails(val name: String, val version: String)
//data class PredictionDetails(val message: String)

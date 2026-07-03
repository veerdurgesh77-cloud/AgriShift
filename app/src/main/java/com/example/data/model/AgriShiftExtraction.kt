package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgriShiftExtraction(
    val workflow: String,
    @Json(name = "machinery_type") val machineryType: String?,
    @Json(name = "land_size_acres") val landSizeAcres: Double?,
    @Json(name = "crop_type") val cropType: String?,
    @Json(name = "urgency_level") val urgencyLevel: String?,
    val location: String?,
    val budget: Double?,
    @Json(name = "raw_crop_condition") val rawCropCondition: String?,
    @Json(name = "voice_command_action") val voiceCommandAction: String?,
    @Json(name = "extracted_parameters") val extractedParameters: ExtractedParameters?,
    @Json(name = "estimated_cost_inr") val estimatedCostInr: Double?,
    @Json(name = "estimated_time_hours") val estimatedTimeHours: Double?,
    @Json(name = "owner_id") val ownerId: Int? = null,
    @Json(name = "available_dates") val availableDates: List<String>? = null,
    @Json(name = "available_hours") val availableHours: String? = null,
    @Json(name = "location_radius") val locationRadius: Double? = null
)

@JsonClass(generateAdapter = true)
data class ExtractedParameters(
    @Json(name = "original_land_value") val originalLandValue: String?,
    @Json(name = "original_language") val originalLanguage: String?,
    val season: String?
)

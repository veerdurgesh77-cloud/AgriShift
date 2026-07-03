package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerationConfig
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.local.AgriShiftDao
import com.example.data.local.AgriShiftEntity
import com.example.data.model.AgriShiftExtraction
import com.example.data.model.ExtractedParameters
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class AgriShiftRepository(private val dao: AgriShiftDao) {

    val history: Flow<List<AgriShiftEntity>> = dao.getAllHistory()

    suspend fun processInput(rawInput: String): AgriShiftExtraction = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val isApiKeyValid = apiKey.isNotEmpty() && !apiKey.startsWith("MY_") && apiKey != "placeholder"

        var extraction: AgriShiftExtraction? = null
        var jsonResponseString = ""

        if (isApiKeyValid) {
            try {
                val systemPrompt = """
                    You are the core backend intelligence engine for "AgriShift," a hyper-local marketplace connecting farmers with heavy machinery owners (harvesters, tractors, specialized equipment). Your job is to process user inputs (which can be messy, typed in a mix of English and regional languages like Marathi or Hindi, or transcribed from voice notes) and turn them into structured, actionable JSON data for the app's frontend.

                    Always output strict, valid JSON. Do not include any markdown formatting like ```json or trailing explanations outside the JSON object.
                    If data is missing from the user's request, set the JSON field to null instead of guessing.
                    Maintain localized context (e.g., understand terms like 'Vigat', 'Acre', 'Bigha', 'Rabi', 'Kharif').

                    Return a single JSON object with this exact schema:
                    {
                      "workflow": "MATCHING" | "CALCULATIONS" | "VOICE_TO_COMMAND" | "EQUIPMENT_AVAILABILITY",
                      "machinery_type": string or null (e.g., "Combine Harvester", "Tractor", "Rotavator", "Plough"),
                      "land_size_acres": float or null (convert Vigat/Bigha to acres: 1 Bigha = 0.4 acres, 1 Vigat = 0.4 acres if not specified, otherwise map directly),
                      "crop_type": string or null (e.g., "Paddy", "Wheat", "Sugarcane", "Soybean", "Cotton"),
                      "urgency_level": "LOW" | "MEDIUM" | "HIGH" or null,
                      "location": string or null,
                      "budget": float or null,
                      "raw_crop_condition": string or null,
                      "voice_command_action": "BOOK_MACHINERY" | "ESTIMATE_COST" | "CONTACT_OWNER" | "VIEW_MARKETPLACE" or null,
                      "extracted_parameters": {
                        "original_land_value": string or null,
                        "original_language": "Marathi" | "Hindi" | "English" | "Mixed" or null,
                        "season": "RABI" | "KHARIF" or null
                      },
                      "estimated_cost_inr": float or null,
                      "estimated_time_hours": float or null,
                      "owner_id": integer or null,
                      "available_dates": array of strings or null (list of date ranges e.g. ["2026-07-05 to 2026-07-10"]),
                      "available_hours": string or null (e.g. "9AM-6PM"),
                      "location_radius": float or null (in km, e.g. 15.0)
                    }

                    TASK-SPECIFIC LOGIC:
                    1. For booking/matching requests: Extract 'machinery_type', 'land_size_acres', 'crop_type', 'urgency_level', and 'location'. Map voice_command_action to "BOOK_MACHINERY" or "VIEW_MARKETPLACE".
                    2. For cost estimations, apply standard baseline estimates in your calculation fields (estimated_cost_inr, estimated_time_hours):
                       - Combine Harvester (Paddy/Wheat): 3000 INR per acre. Takes 1 hour per acre.
                       - Tractor Ploughing: 1300 INR per acre. Takes 1.5 hours per acre.
                       - Rotavator: 1600 INR per acre. Takes 1.2 hours per acre.
                       If no machinery match is specified, but land size is provided, assume Combine Harvester as default for Wheat/Paddy harvesting, or Tractor for generic land preparation.
                    3. For equipment availability registration: If the input describes equipment being available, dates, hours, radius, or owner registration of availability, set "workflow" to "EQUIPMENT_AVAILABILITY". Extract the machinery type, owner ID (if mentioned, map to an integer), the available date ranges as a list of strings (e.g., ["2026-07-05 to 2026-07-10"]), available hours (e.g. "9AM-6PM"), and location radius (e.g. 15.0).
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = rawInput)))),
                    generationConfig = GenerationConfig(
                        responseMimeType = "application/json",
                        temperature = 0.1f
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!rawText.isNullOrEmpty()) {
                    jsonResponseString = rawText.trim()
                    // Remove code fences if Gemini ignores responseMimeType
                    if (jsonResponseString.startsWith("```")) {
                        jsonResponseString = jsonResponseString
                            .removePrefix("```json")
                            .removePrefix("```")
                            .removeSuffix("```")
                            .trim()
                    }
                    val adapter = RetrofitClient.genericMoshi.adapter(AgriShiftExtraction::class.java)
                    extraction = adapter.fromJson(jsonResponseString)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Fail-safe to offline rule-based parser on network exception
            }
        }

        // If live API was not used, or if parsing failed, run local rule-based parsing
        if (extraction == null) {
            extraction = parseLocally(rawInput)
            val adapter = RetrofitClient.genericMoshi.adapter(AgriShiftExtraction::class.java)
            jsonResponseString = adapter.toJson(extraction)
        }

        // Save execution to Room database
        val entity = AgriShiftEntity(
            timestamp = System.currentTimeMillis(),
            rawInput = rawInput,
            workflow = extraction.workflow,
            machineryType = extraction.machineryType,
            landSizeAcres = extraction.landSizeAcres,
            cropType = extraction.cropType,
            urgencyLevel = extraction.urgencyLevel,
            location = extraction.location,
            budget = extraction.budget,
            voiceCommandAction = extraction.voiceCommandAction,
            originalLanguage = extraction.extractedParameters?.originalLanguage,
            originalLandValue = extraction.extractedParameters?.originalLandValue,
            season = extraction.extractedParameters?.season,
            estimatedCostInr = extraction.estimatedCostInr,
            estimatedTimeHours = extraction.estimatedTimeHours,
            jsonResponse = jsonResponseString
        )
        dao.insert(entity)

        return@withContext extraction
    }

    private fun parseLocally(rawInput: String): AgriShiftExtraction {
        val inputLower = rawInput.lowercase()

        // 1. Language Detection
        val isMarathi = inputLower.contains("माझ्याकडे") || inputLower.contains("कापणी") || inputLower.contains("एकर") || inputLower.contains("पाहिजे") || inputLower.contains("रब्बी") || inputLower.contains("खरिप")
        val isHindi = inputLower.contains("बीघा") || inputLower.contains("चाहिए") || inputLower.contains("कटाई") || inputLower.contains("धान") || inputLower.contains("गेहूं") || inputLower.contains("जुताई") || inputLower.contains("tractor")
        val originalLanguage = when {
            isMarathi && isHindi -> "Mixed"
            isMarathi -> "Marathi"
            isHindi -> "Hindi"
            else -> "English"
        }

        // 2. Extract land size & original land value
        var landSizeAcres: Double? = null
        var originalLandValue: String? = null

        val bighaPattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*(bigha|बीघा|विघत|vigat)", Pattern.CASE_INSENSITIVE)
        val acrePattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*(acre|एकर|एकड़)", Pattern.CASE_INSENSITIVE)

        val bighaMatcher = bighaPattern.matcher(inputLower)
        val acreMatcher = acrePattern.matcher(inputLower)

        if (acreMatcher.find()) {
            val value = acreMatcher.group(1)?.toDoubleOrNull()
            if (value != null) {
                landSizeAcres = value
                originalLandValue = "$value Acre"
            }
        } else if (bighaMatcher.find()) {
            val value = bighaMatcher.group(1)?.toDoubleOrNull()
            if (value != null) {
                landSizeAcres = value * 0.4 // 1 Bigha / Vigat approx 0.4 Acres
                originalLandValue = "$value Bigha"
            }
        } else {
            // Find any floating number
            val numPattern = Pattern.compile("(\\d+(\\.\\d+)?)")
            val numMatcher = numPattern.matcher(inputLower)
            if (numMatcher.find()) {
                val value = numMatcher.group(1)?.toDoubleOrNull()
                if (value != null) {
                    landSizeAcres = value
                    originalLandValue = "$value Acre"
                }
            }
        }

        // 3. Crop Type Detection
        val cropType = when {
            inputLower.contains("wheat") || inputLower.contains("गहू") || inputLower.contains("गेहूं") -> "Wheat"
            inputLower.contains("paddy") || inputLower.contains("dhaan") || inputLower.contains("धान") || inputLower.contains("भात") -> "Paddy"
            inputLower.contains("sugarcane") || inputLower.contains("ऊस") || inputLower.contains("गन्ना") -> "Sugarcane"
            inputLower.contains("soybean") || inputLower.contains("सोयाबीन") -> "Soybean"
            inputLower.contains("cotton") || inputLower.contains("कापूस") || inputLower.contains("कपास") -> "Cotton"
            else -> null
        }

        // 4. Machinery Type Detection
        val machineryType = when {
            inputLower.contains("harvester") || inputLower.contains("हार्वेस्टर") || inputLower.contains("कंबाइन") -> "Combine Harvester"
            inputLower.contains("rotavator") || inputLower.contains("रोटाव्हेटर") || inputLower.contains("रोटावेटर") -> "Rotavator"
            inputLower.contains("plough") || inputLower.contains("नांगर") || inputLower.contains("हल") -> "Plough"
            inputLower.contains("tractor") || inputLower.contains("ट्रॅक्टर") || inputLower.contains("ट्रैक्टर") -> "Tractor"
            else -> "Combine Harvester" // default fallback
        }

        // 5. Urgency Level
        val urgencyLevel = when {
            inputLower.contains("urgent") || inputLower.contains("लवकर") || inputLower.contains("जल्दी") || inputLower.contains("कल") || inputLower.contains("tomorrow") -> "HIGH"
            inputLower.contains("week") || inputLower.contains("हफ्ता") || inputLower.contains("आठवडा") -> "LOW"
            else -> "MEDIUM"
        }

        // 6. Location Detection
        val location = when {
            inputLower.contains("pune") || inputLower.contains("पुणे") -> "Pune, MH"
            inputLower.contains("nashik") || inputLower.contains("नाशिक") -> "Nashik, MH"
            inputLower.contains("satara") || inputLower.contains("सातारा") -> "Satara, MH"
            inputLower.contains("nagpur") || inputLower.contains("नागपूर") -> "Nagpur, MH"
            inputLower.contains("kolhapur") || inputLower.contains("कोल्हापूर") -> "Kolhapur, MH"
            inputLower.contains("patna") || inputLower.contains("पटना") -> "Patna, BR"
            inputLower.contains("indore") || inputLower.contains("इंदौर") -> "Indore, MP"
            else -> "Amravati, MH" // default localization
        }

        // 7. Workflow & Voice Actions
        val isAvailability = inputLower.contains("available") || inputLower.contains("availability") || inputLower.contains("avail") || inputLower.contains("radius") || inputLower.contains("km") || inputLower.contains("range") || inputLower.contains("hours") || inputLower.contains("उपलब्ध") || inputLower.contains("तारीख")
        val isVoiceCommand = inputLower.contains("book") || inputLower.contains("call") || inputLower.contains("phon") || inputLower.contains("संपर्क") || inputLower.contains("काढायचं") || inputLower.contains("लाव")
        val isCalculation = inputLower.contains("cost") || inputLower.contains("price") || inputLower.contains("estimation") || inputLower.contains("दर") || inputLower.contains("किती") || inputLower.contains("पैसे") || inputLower.contains("खर्च")

        val workflow = when {
            isAvailability -> "EQUIPMENT_AVAILABILITY"
            isVoiceCommand -> "VOICE_TO_COMMAND"
            isCalculation -> "CALCULATIONS"
            else -> "MATCHING"
        }

        val voiceCommandAction = when {
            isAvailability -> "VIEW_MARKETPLACE"
            inputLower.contains("book") || inputLower.contains("कापणी") -> "BOOK_MACHINERY"
            inputLower.contains("cost") || inputLower.contains("किती") || inputLower.contains("खर्च") -> "ESTIMATE_COST"
            inputLower.contains("call") || inputLower.contains("phone") || inputLower.contains("संपर्क") -> "CONTACT_OWNER"
            else -> "VIEW_MARKETPLACE"
        }

        // Availability extractions
        var extractedOwnerId: Int? = null
        var extractedDates: List<String>? = null
        var extractedHours: String? = null
        var extractedRadius: Double? = null

        if (isAvailability) {
            val ownerPattern = Pattern.compile("(?:owner|id|operator|operator_id)\\s*[:=]?\\s*([1-7])", Pattern.CASE_INSENSITIVE)
            val ownerMatcher = ownerPattern.matcher(inputLower)
            if (ownerMatcher.find()) {
                extractedOwnerId = ownerMatcher.group(1)?.toIntOrNull()
            } else if (inputLower.contains("ramesh")) extractedOwnerId = 1
            else if (inputLower.contains("sanjay")) extractedOwnerId = 2
            else if (inputLower.contains("gurpreet")) extractedOwnerId = 3
            else if (inputLower.contains("vijay")) extractedOwnerId = 4
            else if (inputLower.contains("anil")) extractedOwnerId = 5
            else if (inputLower.contains("ramsevak")) extractedOwnerId = 6
            else if (inputLower.contains("devendra")) extractedOwnerId = 7
            else {
                val digitPattern = Pattern.compile("\\b([1-7])\\b")
                val digitMatcher = digitPattern.matcher(inputLower)
                if (digitMatcher.find()) {
                    extractedOwnerId = digitMatcher.group(1)?.toIntOrNull()
                } else {
                    extractedOwnerId = 3 // default Gurpreet
                }
            }

            val datePattern = Pattern.compile("(\\d{1,2}(?:\\s*(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*|\\s*[-/\\.]\\s*\\d{1,2})[^\\d]*(?:to|and|-)[^\\d]*\\d{1,2}(?:\\s*(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*|\\s*[-/\\.]\\s*\\d{1,2}))", Pattern.CASE_INSENSITIVE)
            val dateMatcher = datePattern.matcher(inputLower)
            if (dateMatcher.find()) {
                extractedDates = listOf(dateMatcher.group(1).trim())
            } else {
                extractedDates = listOf("2026-07-05 to 2026-07-12")
            }

            val hoursPattern = Pattern.compile("(\\d{1,2}\\s*(?:am|pm)\\s*(?:to|-)\\s*\\d{1,2}\\s*(?:am|pm))", Pattern.CASE_INSENSITIVE)
            val hoursMatcher = hoursPattern.matcher(inputLower)
            if (hoursMatcher.find()) {
                extractedHours = hoursMatcher.group(1).uppercase()
            } else {
                extractedHours = "9AM-6PM"
            }

            val radiusPattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*(?:km|radius|kilometer|किलोमीटर)", Pattern.CASE_INSENSITIVE)
            val radiusMatcher = radiusPattern.matcher(inputLower)
            if (radiusMatcher.find()) {
                extractedRadius = radiusMatcher.group(1)?.toDoubleOrNull()
            } else {
                extractedRadius = 15.0
            }
        }

        // 8. Baseline Calculations
        val acres = landSizeAcres ?: 1.0
        val (rate, timePerAcre) = when (machineryType) {
            "Combine Harvester" -> 3000.0 to 1.0
            "Tractor" -> 1300.0 to 1.5
            "Rotavator" -> 1600.0 to 1.2
            "Plough" -> 1300.0 to 1.5
            else -> 3000.0 to 1.0
        }

        val estimatedCostInr = rate * acres
        val estimatedTimeHours = timePerAcre * acres

        val season = when {
            inputLower.contains("rabi") || inputLower.contains("रब्बी") || inputLower.contains("गेहूं") || inputLower.contains("wheat") -> "RABI"
            inputLower.contains("kharif") || inputLower.contains("खरिप") || inputLower.contains("paddy") || inputLower.contains("soybean") -> "KHARIF"
            else -> null
        }

        return AgriShiftExtraction(
            workflow = workflow,
            machineryType = machineryType,
            landSizeAcres = landSizeAcres,
            cropType = cropType,
            urgencyLevel = urgencyLevel,
            location = location,
            budget = null,
            rawCropCondition = "Standard crop density",
            voiceCommandAction = voiceCommandAction,
            extractedParameters = ExtractedParameters(
                originalLandValue = originalLandValue,
                originalLanguage = originalLanguage,
                season = season
            ),
            estimatedCostInr = estimatedCostInr,
            estimatedTimeHours = estimatedTimeHours,
            ownerId = extractedOwnerId,
            availableDates = extractedDates,
            availableHours = extractedHours,
            locationRadius = extractedRadius
        )
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }
}

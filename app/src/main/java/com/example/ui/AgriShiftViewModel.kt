package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AgriShiftDatabase
import com.example.data.local.AgriShiftEntity
import com.example.data.model.AgriShiftExtraction
import com.example.data.model.MachineryData
import com.example.data.model.MachineryOwner
import com.example.data.repository.AgriShiftRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AgriShiftViewModel(private val repository: AgriShiftRepository) : ViewModel() {

    private val _rawInputText = MutableStateFlow("")
    val rawInputText: StateFlow<String> = _rawInputText.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _lastExtraction = MutableStateFlow<AgriShiftExtraction?>(null)
    val lastExtraction: StateFlow<AgriShiftExtraction?> = _lastExtraction.asStateFlow()

    private val defaultAvailabilities = listOf(
        AgriShiftExtraction(
            workflow = "EQUIPMENT_AVAILABILITY",
            machineryType = "Combine Harvester",
            landSizeAcres = null,
            cropType = null,
            urgencyLevel = null,
            location = "Satara, MH",
            budget = null,
            rawCropCondition = null,
            voiceCommandAction = "VIEW_MARKETPLACE",
            extractedParameters = null,
            estimatedCostInr = null,
            estimatedTimeHours = null,
            ownerId = 3, // Gurpreet Singh
            availableDates = listOf("2026-07-05 to 2026-07-12"),
            availableHours = "9AM-6PM",
            locationRadius = 15.0
        ),
        AgriShiftExtraction(
            workflow = "EQUIPMENT_AVAILABILITY",
            machineryType = "Tractor",
            landSizeAcres = null,
            cropType = null,
            urgencyLevel = null,
            location = "Nashik, MH",
            budget = null,
            rawCropCondition = null,
            voiceCommandAction = "VIEW_MARKETPLACE",
            extractedParameters = null,
            estimatedCostInr = null,
            estimatedTimeHours = null,
            ownerId = 4, // Vijay Deshmukh
            availableDates = listOf("2026-07-08 to 2026-07-15"),
            availableHours = "8AM-5PM",
            locationRadius = 20.0
        )
    )

    private val _registeredAvailabilities = MutableStateFlow<List<AgriShiftExtraction>>(defaultAvailabilities)
    val registeredAvailabilities: StateFlow<List<AgriShiftExtraction>> = _registeredAvailabilities.asStateFlow()

    private val _allOwners = MutableStateFlow<List<MachineryOwner>>(MachineryData.sampleOwners)
    val allOwners: StateFlow<List<MachineryOwner>> = _allOwners.asStateFlow()

    private val _matchedOwners = MutableStateFlow<List<MachineryOwner>>(MachineryData.sampleOwners)
    val matchedOwners: StateFlow<List<MachineryOwner>> = _matchedOwners.asStateFlow()

    private val _historyList = MutableStateFlow<List<AgriShiftEntity>>(emptyList())
    val historyList: StateFlow<List<AgriShiftEntity>> = _historyList.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Workspace, 1: Marketplace, 2: History
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _simulatedActionMessage = MutableStateFlow<String?>(null)
    val simulatedActionMessage: StateFlow<String?> = _simulatedActionMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.history.collect { entities ->
                _historyList.value = entities
                val adapter = com.example.data.api.RetrofitClient.genericMoshi.adapter(AgriShiftExtraction::class.java)
                val persistedAvailabilities = entities.filter { it.workflow == "EQUIPMENT_AVAILABILITY" }
                    .mapNotNull { entity ->
                        try {
                            adapter.fromJson(entity.jsonResponse)
                        } catch (e: Exception) {
                            null
                        }
                    }
                _registeredAvailabilities.value = defaultAvailabilities + persistedAvailabilities
                applyFilters()
            }
        }
    }

    fun updateRawInput(text: String) {
        _rawInputText.value = text
    }

    fun setTab(index: Int) {
        _selectedTab.value = index
    }

    fun dismissActionMessage() {
        _simulatedActionMessage.value = null
    }

    fun processInput(textToProcess: String = _rawInputText.value) {
        if (textToProcess.isBlank()) return

        viewModelScope.launch {
            _isProcessing.value = true
            _errorMessage.value = null
            try {
                val result = repository.processInput(textToProcess)
                _lastExtraction.value = result
                if (result.workflow == "EQUIPMENT_AVAILABILITY") {
                    val owner = _allOwners.value.find { it.id == result.ownerId }
                    val ownerName = owner?.ownerName ?: "Operator"
                    _simulatedActionMessage.value = "✅ Registered availability for $ownerName's ${result.machineryType ?: "Equipment"}: dates ${result.availableDates?.joinToString(", ") ?: "Anytime"}, hours ${result.availableHours ?: "9AM-6PM"}, within ${result.locationRadius?.toInt() ?: 15}km. This operator is now prioritized!"
                } else {
                    filterOwners(result)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to process input"
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
        applyFilters()
    }

    fun applyFilters() {
        val query = _searchQuery.value
        val category = _selectedCategory.value
        val extraction = _lastExtraction.value

        var filtered = _allOwners.value

        // 1. Filter by last AI extraction if present
        if (extraction != null) {
            val extractedType = extraction.machineryType
            val location = extraction.location
            filtered = filtered.filter { owner ->
                val matchesType = extractedType == null || 
                        owner.machineryType.equals(extractedType, ignoreCase = true) ||
                        (extractedType.contains("Harvester", ignoreCase = true) && owner.machineryType.contains("Harvester", ignoreCase = true))

                val matchesLocation = location == null || 
                        owner.location.contains(location.substringBefore(","), ignoreCase = true)

                matchesType && matchesLocation
            }.ifEmpty {
                filtered.filter { owner ->
                    extractedType == null || 
                    owner.machineryType.equals(extractedType, ignoreCase = true) ||
                    (extractedType.contains("Harvester", ignoreCase = true) && owner.machineryType.contains("Harvester", ignoreCase = true))
                }
            }
        }

        // 2. Filter by selected category (e.g., Tractor, Combine Harvester)
        if (category != null) {
            filtered = filtered.filter { owner ->
                owner.machineryType.equals(category, ignoreCase = true) ||
                (category.contains("Harvester", ignoreCase = true) && owner.machineryType.contains("Harvester", ignoreCase = true))
            }
        }

        // 3. Filter by typed search query (name, location, model, machinery type)
        if (query.isNotBlank()) {
            filtered = filtered.filter { owner ->
                owner.ownerName.contains(query, ignoreCase = true) ||
                owner.machineryType.contains(query, ignoreCase = true) ||
                owner.modelName.contains(query, ignoreCase = true) ||
                owner.location.contains(query, ignoreCase = true)
            }
        }

        // 4. Prioritize: Move owners with active/registered matching equipment availabilities to the top
        val availList = _registeredAvailabilities.value
        _matchedOwners.value = filtered.sortedByDescending { owner ->
            val isAvailable = availList.any { av ->
                av.ownerId == owner.id && 
                (av.machineryType?.equals(owner.machineryType, ignoreCase = true) == true ||
                 (av.machineryType?.contains("Harvester", ignoreCase = true) == true && owner.machineryType.contains("Harvester", ignoreCase = true)))
            }
            if (isAvailable) 1 else 0
        }
    }

    private fun filterOwners(extraction: AgriShiftExtraction) {
        applyFilters()
    }

    fun startVoiceSimulation(presetText: String) {
        viewModelScope.launch {
            _isRecording.value = true
            _rawInputText.value = "Listening to raw regional audio..."
            delay(1500) // simulate voice recording/streaming
            _rawInputText.value = "Transcribing audio..."
            delay(800)
            _rawInputText.value = presetText
            _isRecording.value = false
            processInput(presetText)
        }
    }

    fun executeSimulatedAction(extraction: AgriShiftExtraction) {
        if (extraction.workflow == "EQUIPMENT_AVAILABILITY") {
            val owner = _allOwners.value.find { it.id == extraction.ownerId } ?: _allOwners.value.first()
            _simulatedActionMessage.value = "✅ Registered availability for owner ${owner.ownerName} (${extraction.machineryType}): Dates ${extraction.availableDates?.joinToString(", ") ?: "Anytime"}, Hours ${extraction.availableHours ?: "9AM-6PM"}, Radius ${extraction.locationRadius ?: 15.0} km. Match prioritization active!"
            return
        }

        val action = extraction.voiceCommandAction ?: "VIEW_MARKETPLACE"
        val machinery = extraction.machineryType ?: "Heavy Machinery"
        val crop = extraction.cropType ?: "crop"
        val loc = extraction.location ?: "Amravati, MH"
        val size = extraction.extractedParameters?.originalLandValue ?: "${extraction.landSizeAcres ?: 5.0} Acres"

        _simulatedActionMessage.value = when (action) {
            "BOOK_MACHINERY" -> "✅ Successfully created booking request for $machinery ($size of $crop) in $loc. Match operators notified!"
            "ESTIMATE_COST" -> "📊 Time and cost calculation generated: Approx. ₹${extraction.estimatedCostInr?.toInt() ?: 15000} for ${extraction.estimatedTimeHours?.toInt() ?: 5} hours. Saved to log."
            "CONTACT_OWNER" -> {
                // Find first matched owner or fallback
                val owner = _matchedOwners.value.firstOrNull() ?: _allOwners.value.first()
                "📞 Initiating regional phone call to owner: ${owner.ownerName} (${owner.modelName}) at ${owner.phoneNumber}."
            }
            "VIEW_MARKETPLACE" -> "🚜 Filtering AgriShift marketplace for available $machinery in $loc. Listings loaded."
            else -> "🚜 Command processed successfully! Local matching options updated."
        }
    }

    fun listNewMachine(
        ownerName: String,
        machineryType: String,
        modelName: String,
        ratePerAcre: Double,
        location: String,
        phoneNumber: String
    ) {
        val nextId = (_allOwners.value.maxOfOrNull { it.id } ?: 0) + 1
        val newOwner = MachineryOwner(
            id = nextId,
            ownerName = ownerName,
            machineryType = machineryType,
            modelName = modelName,
            ratePerAcreInr = ratePerAcre,
            location = location,
            rating = 5.0f,
            phoneNumber = phoneNumber
        )
        _allOwners.value = _allOwners.value + newOwner
        
        // Update matched owners list
        val currentExtraction = _lastExtraction.value
        if (currentExtraction != null) {
            filterOwners(currentExtraction)
        } else {
            _matchedOwners.value = _allOwners.value
        }
        _simulatedActionMessage.value = "🚜 Successfully listed your $machineryType ($modelName) on the marketplace!"
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _lastExtraction.value = null
            _allOwners.value = MachineryData.sampleOwners
            _matchedOwners.value = MachineryData.sampleOwners
            _registeredAvailabilities.value = defaultAvailabilities
        }
    }
}

class AgriShiftViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgriShiftViewModel::class.java)) {
            val database = AgriShiftDatabase.getDatabase(context)
            val repository = AgriShiftRepository(database.agriShiftDao())
            @Suppress("UNCHECKED_CAST")
            return AgriShiftViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

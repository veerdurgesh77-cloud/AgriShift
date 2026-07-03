package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AgriShiftEntity
import com.example.data.model.AgriShiftExtraction
import com.example.data.model.MachineryData
import com.example.data.model.MachineryOwner
import com.example.ui.theme.WarningYellow
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgriShiftApp(viewModel: AgriShiftViewModel) {
    val rawInput by viewModel.rawInputText.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val lastExtraction by viewModel.lastExtraction.collectAsState()
    val matchedOwners by viewModel.matchedOwners.collectAsState()
    val registeredAvailabilities by viewModel.registeredAvailabilities.collectAsState()
    val history by viewModel.historyList.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val actionMessage by viewModel.simulatedActionMessage.collectAsState()

    var showListDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    // User Profile fields:
    var profileName by remember { mutableStateOf("Rahul Patil") }
    var profileBusiness by remember { mutableStateOf("Ashtavinayak Harvesting Center") }
    var profilePhone by remember { mutableStateOf("+91 98765 43210") }
    var profileEmail by remember { mutableStateOf("rahul.patil@agrishift.com") }
    var profileLocationVillage by remember { mutableStateOf("Amravati") }
    var profileLocationDistrict by remember { mutableStateOf("Amravati") }
    var profileLocationState by remember { mutableStateOf("Maharashtra") }
    
    var isOwnerMode by remember { mutableStateOf(false) } // Default: Renter (Farmer) Mode, toggle to true for Owner Mode
    
    // Renter Mode
    var renterLandSize by remember { mutableStateOf("12") }
    var renterCrops by remember { mutableStateOf("Soybean, Wheat, Cotton") }
    val favoriteMachines = remember {
        mutableStateListOf(
            "John Deere 5050D Tractor (Rahul Patil)",
            "Swaraj 744 FE Tractor (Amravati Rental)",
            "Claas Crop Tiger 40 Harvester"
        )
    }
    val mockBookings = remember {
        mutableStateListOf(
            ProfileBooking("John Deere 5050D Tractor", "July 5, 2026", "₹4,500", "Upcoming", Icons.Default.Agriculture),
            ProfileBooking("Swaraj 744 FE Tractor", "June 24, 2026", "₹3,200", "Completed", Icons.Default.CheckCircle),
            ProfileBooking("Claas Crop Tiger 40 Harvester", "June 15, 2026", "₹12,000", "Completed", Icons.Default.Build)
        )
    }
    
    // Owner Mode
    var ownerHpValue by remember { mutableStateOf("50 HP") }
    var ownerModelYear by remember { mutableStateOf("2023") }
    var ownerOperatorIncluded by remember { mutableStateOf(true) }
    val calendarBlockedDays = remember { mutableStateListOf(4, 5, 12, 13, 20) }
    var walletBalance by remember { mutableStateOf(8400.0) }
    var bankAccountNo by remember { mutableStateOf("918273645012") }
    var bankUpiId by remember { mutableStateOf("rahulpatil@okaxis") }
    var kycVerified by remember { mutableStateOf(true) }
    var kycDocType by remember { mutableStateOf("Aadhaar Card") }
    
    val myMachineryFleet = remember {
        mutableStateListOf(
            ProfileMachine("John Deere 5050D", "Tractor", "50 HP", "2023", "₹1,500/hr", "Active"),
            ProfileMachine("Swaraj 744 FE", "Tractor", "48 HP", "2022", "₹1,200/hr", "Active")
        )
    }

    if (showProfileDialog) {
        UserProfileDialog(
            profileName = profileName,
            onProfileNameChange = { profileName = it },
            profileBusiness = profileBusiness,
            onProfileBusinessChange = { profileBusiness = it },
            profilePhone = profilePhone,
            onProfilePhoneChange = { profilePhone = it },
            profileEmail = profileEmail,
            onProfileEmailChange = { profileEmail = it },
            profileLocationVillage = profileLocationVillage,
            onProfileLocationVillageChange = { profileLocationVillage = it },
            profileLocationDistrict = profileLocationDistrict,
            onProfileLocationDistrictChange = { profileLocationDistrict = it },
            profileLocationState = profileLocationState,
            onProfileLocationStateChange = { profileLocationState = it },
            isOwnerMode = isOwnerMode,
            onIsOwnerModeChange = { isOwnerMode = it },
            renterLandSize = renterLandSize,
            onRenterLandSizeChange = { renterLandSize = it },
            renterCrops = renterCrops,
            onRenterCropsChange = { renterCrops = it },
            ownerHpValue = ownerHpValue,
            onOwnerHpValueChange = { ownerHpValue = it },
            ownerModelYear = ownerModelYear,
            onOwnerModelYearChange = { ownerModelYear = it },
            ownerOperatorIncluded = ownerOperatorIncluded,
            onOwnerOperatorIncludedChange = { ownerOperatorIncluded = it },
            walletBalance = walletBalance,
            onWalletBalanceChange = { walletBalance = it },
            bankAccountNo = bankAccountNo,
            onBankAccountNoChange = { bankAccountNo = it },
            bankUpiId = bankUpiId,
            onBankUpiIdChange = { bankUpiId = it },
            kycVerified = kycVerified,
            onKycVerifiedChange = { kycVerified = it },
            kycDocType = kycDocType,
            onKycDocTypeChange = { kycDocType = it },
            mockBookings = mockBookings,
            favoriteMachines = favoriteMachines,
            calendarBlockedDays = calendarBlockedDays,
            myMachineryFleet = myMachineryFleet,
            onDismiss = { showProfileDialog = false }
        )
    }

    if (showListDialog) {
        var ownerName by remember { mutableStateOf("") }
        var machineryType by remember { mutableStateOf("Tractor") }
        var modelName by remember { mutableStateOf("") }
        var ratePerAcre by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("Amravati, MH") }
        var phoneNumber by remember { mutableStateOf("") }
        var isDropdownExpanded by remember { mutableStateOf(false) }

        val machineryTypes = listOf("Tractor", "Combine Harvester", "Rotavator", "Plough", "Power Tiller", "Seed Drill")

        AlertDialog(
            onDismissRequest = { showListDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Agriculture,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "List Your Heavy Machinery",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Enter details to offer your equipment to regional farmers in need.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("Owner / Operator Name") },
                        placeholder = { Text("e.g. Ramesh Patil") },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("add_owner_name"),
                        singleLine = true
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = machineryType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Machinery Type") },
                            leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { isDropdownExpanded = !isDropdownExpanded }) {
                                    Icon(
                                        imageVector = if (isDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = "Toggle Dropdown"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().clickable { isDropdownExpanded = !isDropdownExpanded }.testTag("add_machinery_type")
                        )
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            machineryTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        machineryType = type
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = modelName,
                        onValueChange = { modelName = it },
                        label = { Text("Model / Spec Name") },
                        placeholder = { Text("e.g. John Deere 5050D") },
                        leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("add_model_name"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = ratePerAcre,
                        onValueChange = { ratePerAcre = it },
                        label = { Text("Rate per Acre (INR)") },
                        placeholder = { Text("e.g. 1500") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 4.dp)) },
                        modifier = Modifier.fillMaxWidth().testTag("add_rate_per_acre"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Operating Location") },
                        placeholder = { Text("e.g. Amravati, MH") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("add_location"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Contact Phone Number") },
                        placeholder = { Text("e.g. +91 98765 43210") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("add_phone_number"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rate = ratePerAcre.toDoubleOrNull() ?: 1500.0
                        viewModel.listNewMachine(
                            ownerName.ifBlank { "New Operator" },
                            machineryType,
                            modelName.ifBlank { "Standard Spec" },
                            rate,
                            location.ifBlank { "Amravati, MH" },
                            phoneNumber.ifBlank { "+91 99999 88888" }
                        )
                        showListDialog = false
                    },
                    modifier = Modifier.testTag("add_submit_button")
                ) {
                    Text("Submit Listing")
                }
            },
            dismissButton = {
                TextButton(onClick = { showListDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Check if live API is working or placeholder
    val apiKey = com.example.BuildConfig.GEMINI_API_KEY
    val isLiveApiKey = apiKey.isNotEmpty() && !apiKey.startsWith("MY_") && apiKey != "placeholder"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showProfileDialog = true }
                            .padding(4.dp)
                            .testTag("topbar_logo_profile_click")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary
                                        )
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials = if (profileName.isNotBlank()) {
                                profileName.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString("").uppercase()
                            } else "RP"
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(14.dp)
                                    .background(MaterialTheme.colorScheme.tertiary, shape = CircleShape)
                                    .border(1.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Agriculture,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(8.dp)
                                )
                            }
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "AgriShift",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Profile Options",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "Hello, $profileName • ${if (isOwnerMode) "Owner Mode" else "Farmer Mode"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.clearHistory() },
                        modifier = Modifier.testTag("clear_all_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear History",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            Column {
                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.setTab(0) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        modifier = Modifier.testTag("tab_home")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.setTab(1) },
                        icon = { Icon(Icons.Default.Storefront, contentDescription = "Marketplace") },
                        label = { Text("Marketplace") },
                        modifier = Modifier.testTag("tab_marketplace")
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { showListDialog = true },
                        icon = { Icon(Icons.Default.AddCircle, contentDescription = "List Machine") },
                        label = { Text("List Machine") },
                        modifier = Modifier.testTag("tab_list_machine")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { viewModel.setTab(2) },
                        icon = { Icon(Icons.Default.History, contentDescription = "History Logs") },
                        label = { Text("History") },
                        modifier = Modifier.testTag("tab_history")
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main content based on Tab
            when (selectedTab) {
                0 -> HomeTab(
                    viewModel = viewModel,
                    mockBookings = mockBookings,
                    onNavigateToMarketplace = { viewModel.setTab(1) },
                    onBookOwner = { owner, isBooked ->
                        if (isBooked) {
                            val exists = mockBookings.any { it.title.startsWith(owner.modelName) }
                            if (!exists) {
                                mockBookings.add(
                                    ProfileBooking(
                                        title = "${owner.modelName} (${owner.ownerName})",
                                        date = "July 5, 2026",
                                        cost = "₹${owner.ratePerAcreInr.toInt()}/Acre",
                                        status = "Upcoming",
                                        icon = androidx.compose.material.icons.Icons.Default.Agriculture
                                    )
                                )
                            }
                        } else {
                            mockBookings.removeAll { it.title.startsWith(owner.modelName) }
                        }
                    }
                )
                1 -> MarketplaceTab(
                    matchedOwners = matchedOwners,
                    lastExtraction = lastExtraction,
                    registeredAvailabilities = registeredAvailabilities,
                    mockBookings = mockBookings,
                    onBookOwner = { owner, isBooked ->
                        if (isBooked) {
                            val exists = mockBookings.any { it.title.startsWith(owner.modelName) }
                            if (!exists) {
                                mockBookings.add(
                                    ProfileBooking(
                                        title = "${owner.modelName} (${owner.ownerName})",
                                        date = "July 5, 2026",
                                        cost = "₹${owner.ratePerAcreInr.toInt()}/Acre",
                                        status = "Upcoming",
                                        icon = androidx.compose.material.icons.Icons.Default.Agriculture
                                    )
                                )
                            }
                        } else {
                            mockBookings.removeAll { it.title.startsWith(owner.modelName) }
                        }
                    }
                )
                2 -> HistoryTab(
                    history = history,
                    mockBookings = mockBookings,
                    onRateBooking = { updatedBooking ->
                        val index = mockBookings.indexOfFirst { it.title == updatedBooking.title && it.date == updatedBooking.date }
                        if (index != -1) {
                            mockBookings[index] = updatedBooking
                        }
                    },
                    onItemClick = { entity ->
                        viewModel.updateRawInput(entity.rawInput)
                        viewModel.setTab(0)
                        viewModel.processInput(entity.rawInput)
                    }
                )
            }

            // Simulated Action Notification Overlay
            actionMessage?.let { msg ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                        .testTag("action_overlay"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Notification",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Action Triggered",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        IconButton(onClick = { viewModel.dismissActionMessage() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

data class MatchRecommendation(
    val machineryType: String,
    val attachmentSize: String,
    val description: String,
    val keySpecification: String,
    val targetCategory: String
)

fun getMatchingRecommendation(crop: String, soil: String): MatchRecommendation {
    return when (crop) {
        "Sugarcane" -> {
            when (soil) {
                "Black Clay" -> MatchRecommendation(
                    machineryType = "Heavy Duty Tractor (55HP+)",
                    attachmentSize = "Deep Subsoiler (2-tyne / 3-tyne)",
                    description = "Sugarcane root depth requires deep tilling. Black clay is extremely dense, needing high torque 55HP+ tractors to pull subsoilers without clogging.",
                    keySpecification = "Min. 55HP, Category II 3-point hitch, 8-inch tilling depth",
                    targetCategory = "Tractor"
                )
                "Sandy" -> MatchRecommendation(
                    machineryType = "Medium Tractor (45HP)",
                    attachmentSize = "Disc Harrow (14-disc / 16-disc)",
                    description = "Sandy soils have low compaction and drain quickly. A medium-horsepower tractor with a disc harrow prepares the bed perfectly without sinking.",
                    keySpecification = "45HP, 14-disc configuration, depth control skid",
                    targetCategory = "Tractor"
                )
                else -> MatchRecommendation(
                    machineryType = "Tractor (50HP)",
                    attachmentSize = "Rotavator (6-feet)",
                    description = "Standard loam or general soil with sugarcane planting requires secondary tillage using a 6-feet rotavator to pulverize clods.",
                    keySpecification = "50HP, 36-blade rotavator, multi-speed gearbox",
                    targetCategory = "Rotavator"
                )
            }
        }
        "Cotton" -> {
            when (soil) {
                "Black Clay" -> MatchRecommendation(
                    machineryType = "Tractor (50HP+)",
                    attachmentSize = "Disc Plough (3-bottom)",
                    description = "Cotton roots leave tough residues. Black clay requires a strong 3-bottom disc plough to cut and bury stalks, preventing pest breeding.",
                    keySpecification = "50HP+, 26-inch steel discs, adjustable scrapers",
                    targetCategory = "Plough"
                )
                "Sandy" -> MatchRecommendation(
                    machineryType = "Tractor (45HP)",
                    attachmentSize = "Seed Drill (9-row automatic)",
                    description = "Sandy soil is easy to till but highly prone to wind erosion. Sowing cotton seeds directly using an automatic seed drill retains soil structure.",
                    keySpecification = "45HP, 9-row direct metering system, depth control wheel",
                    targetCategory = "Seed Drill"
                )
                else -> MatchRecommendation(
                    machineryType = "Tractor (45-50HP)",
                    attachmentSize = "Rotavator (5.5-feet)",
                    description = "Generates a fine seedbed for cotton crops on loamy or mixed soils. High rotor speed is recommended for optimal mixing.",
                    keySpecification = "45HP, 30-blade L-type configuration",
                    targetCategory = "Rotavator"
                )
            }
        }
        "Wheat" -> {
            when (soil) {
                "Alluvial" -> MatchRecommendation(
                    machineryType = "Combine Harvester",
                    attachmentSize = "14-feet Cutter Bar Harvester",
                    description = "Wheat in alluvial plains grows densely. A multi-crop combine harvester with a 14-foot cutter bar yields high-throughput clean grain recovery.",
                    keySpecification = "Multi-crop capability, 5-straw walkers, double sieve clean",
                    targetCategory = "Combine Harvester"
                )
                "Sandy" -> MatchRecommendation(
                    machineryType = "Tractor (45HP)",
                    attachmentSize = "9-Tyne Cultivator + Seed Drill",
                    description = "Sandy soils have low draft resistance. A standard 9-tyne cultivator prepares soil fast, followed immediately by a combined seed drill.",
                    keySpecification = "45HP, seed-cum-fertilizer double-box drill",
                    targetCategory = "Seed Drill"
                )
                else -> MatchRecommendation(
                    machineryType = "Combine Harvester",
                    attachmentSize = "12-feet Standard Harvester",
                    description = "Suitable for all-around wheat harvesting across mixed agricultural zones, maximizing grain output and straw collection.",
                    keySpecification = "100HP engine, 12-feet cutter, crop lifters included",
                    targetCategory = "Combine Harvester"
                )
            }
        }
        "Paddy/Rice" -> {
            when (soil) {
                "Clayey / Black" -> MatchRecommendation(
                    machineryType = "Tractor with Cage Wheels (45HP+)",
                    attachmentSize = "Cage Wheels (dual-fitment) + Rotavator",
                    description = "Rice puddling in dense clayey soil requires specialized cage wheel fitments on a 45HP+ tractor to prevent sinking while tilling wet soil.",
                    keySpecification = "Dual row cage wheels, high-clearance sealing, puddling rotavator",
                    targetCategory = "Tractor"
                )
                else -> MatchRecommendation(
                    machineryType = "Power Tiller (12-15HP)",
                    attachmentSize = "Rotary Tiller Attachment (2.5-feet)",
                    description = "Ideal for smaller rice paddies and swampy conditions. A walk-behind power tiller handles muddy soil gracefully without heavy vehicle compaction.",
                    keySpecification = "12HP diesel engine, hand clutch steering, muddy tines",
                    targetCategory = "Power Tiller"
                )
            }
        }
        "Maize" -> {
            when (soil) {
                "Red Soil" -> MatchRecommendation(
                    machineryType = "Tractor (50HP)",
                    attachmentSize = "Rotavator (6-feet) with Heavy Flanges",
                    description = "Red soils often contain stones and form hard surface crusts. A heavy-duty rotavator pulverizes the crust, allowing maize roots to breathe.",
                    keySpecification = "50HP, heavy flange multi-speed rotavator, C-type blades",
                    targetCategory = "Rotavator"
                )
                else -> MatchRecommendation(
                    machineryType = "Tractor (45-50HP)",
                    attachmentSize = "Pneumatic Maize Planter (4-row)",
                    description = "Achieves precise seed spacing and depth control for maize, maximizing germination rate and uniformity on standard agricultural soils.",
                    keySpecification = "Vacuum seed selection, integrated fertilizer hopper, press wheels",
                    targetCategory = "Seed Drill"
                )
            }
        }
        else -> {
            MatchRecommendation(
                machineryType = "Tractor (50HP)",
                attachmentSize = "6-feet Multi-speed Rotavator",
                description = "General purpose tillage for mixed cropping. Highly versatile, providing optimal soil aeration and residual weed tilling.",
                keySpecification = "50HP, multi-speed gearbox, boron steel blades",
                targetCategory = "Rotavator"
            )
        }
    }
}

@Composable
fun SoilCropMatcherCard(
    viewModel: AgriShiftViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCrop by remember { mutableStateOf("Sugarcane") }
    var selectedSoil by remember { mutableStateOf("Black Clay") }
    var filterAppliedText by remember { mutableStateOf<String?>(null) }

    val cropsList = listOf("Sugarcane", "Cotton", "Wheat", "Paddy/Rice", "Maize")
    val soilsList = listOf("Black Clay", "Sandy", "Alluvial", "Red Soil")

    var cropDropdownExpanded by remember { mutableStateOf(false) }
    var soilDropdownExpanded by remember { mutableStateOf(false) }

    val recommendation = getMatchingRecommendation(selectedCrop, selectedSoil)

    Card(
        modifier = modifier.testTag("soil_crop_matcher_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with custom layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "Soil & Crop Machine Matcher",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Find the exact equipment & optimal attachments",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f))

            // Selectors row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Crop dropdown
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Select Crop",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { cropDropdownExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("dropdown_crop_trigger")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedCrop,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expand Crop Menu",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = cropDropdownExpanded,
                            onDismissRequest = { cropDropdownExpanded = false },
                            modifier = Modifier.testTag("dropdown_crop_menu")
                        ) {
                            cropsList.forEach { crop ->
                                DropdownMenuItem(
                                    text = { Text(crop) },
                                    onClick = {
                                        selectedCrop = crop
                                        cropDropdownExpanded = false
                                        filterAppliedText = null // Reset applied text on change
                                    },
                                    modifier = Modifier.testTag("crop_item_$crop")
                                )
                            }
                        }
                    }
                }

                // Soil dropdown
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Soil Type",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { soilDropdownExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("dropdown_soil_trigger")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedSoil,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expand Soil Menu",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = soilDropdownExpanded,
                            onDismissRequest = { soilDropdownExpanded = false },
                            modifier = Modifier.testTag("dropdown_soil_menu")
                        ) {
                            soilsList.forEach { soil ->
                                DropdownMenuItem(
                                    text = { Text(soil) },
                                    onClick = {
                                        selectedSoil = soil
                                        soilDropdownExpanded = false
                                        filterAppliedText = null // Reset applied text on change
                                    },
                                    modifier = Modifier.testTag("soil_item_${soil.replace(" ", "_")}")
                                )
                            }
                        }
                    }
                }
            }

            // Recommendation Result container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "RECOMMENDED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Text(
                            text = recommendation.machineryType,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("recommended_machine_type")
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Attachment Size",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Attachment Spec: ",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = recommendation.attachmentSize,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("recommended_attachment_size")
                        )
                    }

                    Text(
                        text = recommendation.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    // Technical Spec badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Specification",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Specs: ${recommendation.keySpecification}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("recommended_key_specs")
                        )
                    }
                }
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        // Select the recommended category in the view model to filter listings
                        viewModel.selectCategory(recommendation.targetCategory)
                        // Also clear search query so the category filter is dominant and displays matches
                        viewModel.updateSearchQuery("")
                        filterAppliedText = "Showing matched ${recommendation.targetCategory} listings below!"
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("apply_soil_crop_filter_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Filter Match Listings",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        // Clear the dynamic soil/crop filters by resetting category
                        viewModel.selectCategory(null)
                        filterAppliedText = null
                    },
                    modifier = Modifier
                        .height(40.dp)
                        .testTag("reset_soil_crop_filter_btn"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            filterAppliedText?.let { applied ->
                Text(
                    text = "✓ $applied",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .testTag("soil_crop_filter_status")
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTab(
    viewModel: AgriShiftViewModel,
    mockBookings: List<ProfileBooking>,
    onNavigateToMarketplace: () -> Unit,
    onBookOwner: (MachineryOwner, Boolean) -> Unit = { _, _ -> }
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val matchedOwners by viewModel.matchedOwners.collectAsState()
    val registeredAvailabilities by viewModel.registeredAvailabilities.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val rawInput by viewModel.rawInputText.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Search Bar at the Top
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search tractors, harvesters, or locations...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_search_bar"),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp)
        )

        // 2. Beautiful Accent Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Need Heavy Machinery?",
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Instantly rent certified regional equipment or use voice assistant below to match crops.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Agriculture,
                    contentDescription = "Agriculture Banner Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Soil & Crop Machine Matcher
        SoilCropMatcherCard(
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )

        // 3. Equipment Categories Row
        Text(
            text = "Select Equipment to Rent",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        val categories = listOf(
            CategoryItem("All", null, Icons.Default.Apps),
            CategoryItem("Tractor", "Tractor", Icons.Default.Agriculture),
            CategoryItem("Harvester", "Combine Harvester", Icons.Default.Build),
            CategoryItem("Rotavator", "Rotavator", Icons.Default.Build),
            CategoryItem("Plough", "Plough", Icons.Default.Settings),
            CategoryItem("Power Tiller", "Power Tiller", Icons.Default.Build),
            CategoryItem("Seed Drill", "Seed Drill", Icons.Default.Agriculture)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat.typeValue
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectCategory(cat.typeValue) },
                    label = { Text(cat.name) },
                    leadingIcon = {
                        Icon(
                            imageVector = cat.icon,
                            contentDescription = cat.name,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.testTag("category_chip_${cat.name.replace(" ", "_")}")
                )
            }
        }

        // 4. Available Machinery Listings
        Text(
            text = if (selectedCategory != null) "Available ${selectedCategory}s" else "Available Regional Machinery",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (matchedOwners.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SentimentDissatisfied,
                        contentDescription = "No matches",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No equipment matches your current filter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            val displayList = matchedOwners.take(3)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                displayList.forEach { owner ->
                    val availability = registeredAvailabilities.find { av ->
                        av.ownerId == owner.id && 
                        (av.machineryType?.equals(owner.machineryType, ignoreCase = true) == true ||
                         (av.machineryType?.contains("Harvester", ignoreCase = true) == true && owner.machineryType.contains("Harvester", ignoreCase = true)))
                    }
                    val isInitiallyBooked = mockBookings.any { it.title.startsWith(owner.modelName) }
                    OwnerCard(
                        owner = owner,
                        availability = availability,
                        isInitiallyBooked = isInitiallyBooked,
                        onBookStatusChanged = { isBooked ->
                            onBookOwner(owner, isBooked)
                        }
                    )
                }

                if (matchedOwners.size > 3) {
                    Button(
                        onClick = onNavigateToMarketplace,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("View all ${matchedOwners.size} listings in Marketplace")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 5. Expandable Smart Voice Assist Section
        var isVoiceAssistExpanded by remember { mutableStateOf(false) }

        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isVoiceAssistExpanded = !isVoiceAssistExpanded },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Smart Voice",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Smart Voice Assistant",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "Speak or write in regional languages.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = if (isVoiceAssistExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand toggle",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isVoiceAssistExpanded) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // AI Warning
                    val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                    val isLiveApiKey = apiKey.isNotEmpty() && !apiKey.startsWith("MY_") && apiKey != "placeholder"
                    if (!isLiveApiKey) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Localized engine active. Processing locally using local linguistic parsing.",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Quick demos
                    Text(
                        text = "Tap Quick Demos:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetCard(
                            language = "Marathi",
                            title = "Pune Harvest",
                            text = "माझ्याकडे ५ एकर रब्बी गहू कापणीसाठी कंबाइन हार्वेस्टर पाहिजे आहे, लवकरच हवे आहे पुणे परिसरामध्ये.",
                            modifier = Modifier.width(160.dp),
                            onSelect = { viewModel.startVoiceSimulation(it) }
                        )
                        PresetCard(
                            language = "Hinglish",
                            title = "Plough Cost",
                            text = "Need cost estimation for tractor ploughing on 10 Bigha in Patna, urgent.",
                            modifier = Modifier.width(160.dp),
                            onSelect = { viewModel.startVoiceSimulation(it) }
                        )
                        PresetCard(
                            language = "Hindi",
                            title = "Paddy Cut",
                            text = "धान कटाई के लिए कंबाइन हार्वेस्टर चाहिए 8 एकड़ अमरावती में।",
                            modifier = Modifier.width(160.dp),
                            onSelect = { viewModel.startVoiceSimulation(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = rawInput,
                        onValueChange = { viewModel.updateRawInput(it) },
                        placeholder = { Text("Type voice script / query...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isRecording) {
                        AudioWaveform()
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.startVoiceSimulation("माझ्याकडे ५ एकर रब्बी गहू कापणीसाठी कंबाइन हार्वेस्टर पाहिजे आहे, लवकरच हवे आहे पुणे परिसरामध्ये.") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sim. Mic")
                        }
                        Button(
                            onClick = { viewModel.processInput() },
                            modifier = Modifier.weight(1f),
                            enabled = rawInput.isNotBlank() && !isProcessing,
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                            } else {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Process")
                            }
                        }
                    }
                }
            }
        }
    }
}

data class CategoryItem(
    val name: String,
    val typeValue: String?,
    val icon: ImageVector
)

@Composable
fun WorkspaceTab(
    rawInput: String,
    isProcessing: Boolean,
    isRecording: Boolean,
    lastExtraction: AgriShiftExtraction?,
    error: String?,
    isLiveApiKey: Boolean,
    onInputChange: (String) -> Unit,
    onProcess: () -> Unit,
    onVoicePreset: (String) -> Unit,
    onTriggerAction: (AgriShiftExtraction) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Warning if using offline fallback
        if (!isLiveApiKey) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.OfflineBolt,
                        contentDescription = "Offline Mode Active",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Localized Engine Active",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Processing locally using local linguistic parsing. Configure GEMINI_API_KEY in AI Studio Secrets for live multi-turn regional AI translation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Geometric Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = "AgriShift AI Workspace",
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Analyze messy regional voice scripts into high-fidelity structured marketplace coordinates.",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Multi-lingual Scenarios
        Text(
            text = "Tap Quick Demos (Marathi, Hindi & Mixed)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetCard(
                language = "Marathi",
                title = "Harvester Booking",
                text = "माझ्याकडे ५ एकर रब्बी गहू कापणीसाठी कंबाइन हार्वेस्टर पाहिजे आहे, लवकरच हवे आहे पुणे परिसरामध्ये.",
                modifier = Modifier.width(180.dp),
                onSelect = onVoicePreset
            )
            PresetCard(
                language = "Hinglish",
                title = "Cost Calculation",
                text = "Need cost estimation for tractor ploughing on 10 Bigha in Patna, urgent.",
                modifier = Modifier.width(180.dp),
                onSelect = onVoicePreset
            )
            PresetCard(
                language = "Hindi Voice",
                title = "Paddy Harvest",
                text = "धान कटाई के लिए कंबाइन हार्वेस्टर चाहिए 8 एकड़ अमरावती में।",
                modifier = Modifier.width(180.dp),
                onSelect = onVoicePreset
            )
            PresetCard(
                language = "English Reg.",
                title = "Register Harvester",
                text = "Owner 3: Combine Harvester available July 5th to July 12th, hours 9AM-6PM, within 15 km radius.",
                modifier = Modifier.width(180.dp),
                onSelect = onVoicePreset
            )
            PresetCard(
                language = "Hindi Reg.",
                title = "Register Tractor",
                text = "Owner 4: Tractor उपलब्ध है July 8 से July 15 तक, 8AM-5PM, 20 km radius में।",
                modifier = Modifier.width(180.dp),
                onSelect = onVoicePreset
            )
        }

        // Voice Command Input Box
        Text(
            text = "Voice Command or Written Request",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = rawInput,
            onValueChange = onInputChange,
            placeholder = { Text("Type messy farmer inputs or click 'Quick Demos' to stream voice commands...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .testTag("input_text_field"),
            shape = RoundedCornerShape(24.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Live Recording wave visualizer (Simulated)
        if (isRecording) {
            AudioWaveform()
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Simulated Recording Button
            Button(
                onClick = {
                    onVoicePreset("माझ्याकडे ५ एकर रब्बी गहू कापणीसाठी कंबाइन हार्वेस्टर पाहिजे आहे, लवकरच हवे आहे पुणे परिसरामध्ये.")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("simulate_voice_button")
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Simulate Voice Input")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simulate Mic", fontWeight = FontWeight.SemiBold)
            }

            // Process Button
            Button(
                onClick = onProcess,
                enabled = rawInput.isNotBlank() && !isProcessing && !isRecording,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("process_button")
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Process with AI")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Parse with AI", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = error, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }

        // Parse Output Visualization
        lastExtraction?.let { extraction ->
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Structured Intelligence Result",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Extracted workflow type chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                SuggestionChip(
                    onClick = {},
                    label = { Text("Workflow: ${extraction.workflow}") },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (extraction.extractedParameters?.originalLanguage != null) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Language: ${extraction.extractedParameters.originalLanguage}") },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }

            // Visual grid of attributes
            ExtractionGrid(extraction = extraction)

            Spacer(modifier = Modifier.height(16.dp))

            // Availability details card
            AvailabilityDetailsCard(extraction = extraction)

            if (extraction.workflow != "EQUIPMENT_AVAILABILITY") {
                // Baselines Cost estimation card
                CostCalculationCard(extraction = extraction)

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Voice Action executer
                ActionCommandCard(extraction = extraction, onTriggerAction = onTriggerAction)

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Formatted JSON Output card
            RawJsonCard(extraction = extraction)
        }
    }
}

@Composable
fun PresetCard(
    language: String,
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit
) {
    Card(
        modifier = modifier
            .height(105.dp)
            .clickable { onSelect(text) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = language,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AvailabilityDetailsCard(extraction: AgriShiftExtraction) {
    if (extraction.workflow != "EQUIPMENT_AVAILABILITY") return

    Card(
        modifier = Modifier.fillMaxWidth().testTag("availability_details_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EventAvailable,
                    contentDescription = "Availability Details",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Operator Availability Registered",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Machinery Type", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(extraction.machineryType ?: "Unknown", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Owner ID", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val ownerName = MachineryData.sampleOwners.find { it.id == extraction.ownerId }?.ownerName ?: "Gurpreet Singh"
                    Text("ID ${extraction.ownerId ?: 3} ($ownerName)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Available Dates", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(extraction.availableDates?.joinToString(", ") ?: "Anytime", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Available Hours", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(extraction.availableHours ?: "9AM-6PM", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Location Radius", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${extraction.locationRadius?.toInt() ?: 15} km", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Composable
fun AudioWaveform() {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")
    val h1 by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 15f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "h3"
    )
    val h4 by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "h4"
    )
    val h5 by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 55f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "h5"
    )

    val heights = listOf(h1, h2, h3, h4, h5)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recording Regional Audio...  ",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.width(100.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                heights.forEach { height ->
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(height.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        }
    }
}

@Composable
fun ExtractionGrid(extraction: AgriShiftExtraction) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AttributeCard(
                icon = Icons.Default.PrecisionManufacturing,
                title = "Required Machine",
                value = extraction.machineryType ?: "None specified",
                modifier = Modifier.weight(1f)
            )
            AttributeCard(
                icon = Icons.Default.Agriculture,
                title = "Target Crop",
                value = extraction.cropType ?: "None specified",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AttributeCard(
                icon = Icons.Default.Landscape,
                title = "Acreage (Acres)",
                value = if (extraction.landSizeAcres != null) {
                    val original = extraction.extractedParameters?.originalLandValue
                    if (original != null && !original.contains("acre", ignoreCase = true)) {
                        "${extraction.landSizeAcres} ac ($original)"
                    } else {
                        "${extraction.landSizeAcres} ac"
                    }
                } else "None specified",
                modifier = Modifier.weight(1f)
            )
            AttributeCard(
                icon = Icons.Default.PinDrop,
                title = "Location",
                value = extraction.location ?: "None specified",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AttributeCard(
                icon = Icons.Default.Speed,
                title = "Urgency Level",
                value = extraction.urgencyLevel ?: "Medium",
                valueColor = when (extraction.urgencyLevel) {
                    "HIGH" -> MaterialTheme.colorScheme.error
                    "LOW" -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.secondary
                },
                modifier = Modifier.weight(1f)
            )
            AttributeCard(
                icon = Icons.Default.Timer,
                title = "Crop Season",
                value = extraction.extractedParameters?.season ?: "General/Yearly",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AttributeCard(
    icon: ImageVector,
    title: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = valueColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CostCalculationCard(extraction: AgriShiftExtraction) {
    val estimatedCost = extraction.estimatedCostInr
    val estimatedHours = extraction.estimatedTimeHours

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Cost Estimations",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cost & Duration Estimation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "AgriShift Rules Applied",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (estimatedCost != null && estimatedHours != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ESTIMATED TOTAL COST",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "₹${estimatedCost.toInt()}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "ESTIMATED TIME REQUIRED",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "${estimatedHours} Hours",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                Spacer(modifier = Modifier.height(12.dp))

                // Explain math based on rules
                val machine = extraction.machineryType ?: "Combine Harvester"
                val acres = extraction.landSizeAcres ?: 1.0
                val textExplanation = when (machine) {
                    "Combine Harvester" -> "Calculated for Combine Harvester: ₹3,000/acre baseline. Takes 1.0 hour/acre."
                    "Tractor" -> "Calculated for Tractor Ploughing: ₹1,300/acre baseline. Takes 1.5 hours/acre."
                    "Rotavator" -> "Calculated for Rotavator: ₹1,600/acre baseline. Takes 1.2 hours/acre."
                    "Plough" -> "Calculated for Plough: ₹1,300/acre baseline. Takes 1.5 hours/acre."
                    else -> "Calculated based on standard localized baseline: ₹3,000/acre for heavy farm equipment."
                }

                Text(
                    text = textExplanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontFamily = FontFamily.SansSerif
                )
            } else {
                Text(
                    text = "Acreage or machinery type missing. Input land size (e.g. 5 acres) to automatically trigger estimation math.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ActionCommandCard(
    extraction: AgriShiftExtraction,
    onTriggerAction: (AgriShiftExtraction) -> Unit
) {
    val action = extraction.voiceCommandAction ?: "VIEW_MARKETPLACE"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsRun,
                    contentDescription = "Voice Action Dispatcher",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Voice-to-Command Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Informal spoken phrasing successfully translated into formal application instructions:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "TRANSLATED ACTION",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                    Text(
                        text = action,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Button(
                    onClick = { onTriggerAction(extraction) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("trigger_action_button")
                ) {
                    Text("Trigger Action", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Execute",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RawJsonCard(extraction: AgriShiftExtraction) {
    var expanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    // Convert entity to beautiful JSON string
    val jsonString: String = remember(extraction) {
        val adapter = com.example.data.api.RetrofitClient.genericMoshi.adapter(AgriShiftExtraction::class.java).indent("  ")
        adapter.toJson(extraction)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Raw JSON Response",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Strict Structured JSON Output",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(jsonString))
                        },
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy JSON",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E2822), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = jsonString,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFFC8E6C9),
                        style = TextStyle(lineHeight = 16.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun MarketplaceTab(
    matchedOwners: List<MachineryOwner>,
    lastExtraction: AgriShiftExtraction?,
    registeredAvailabilities: List<AgriShiftExtraction> = emptyList(),
    mockBookings: List<ProfileBooking>,
    onBookOwner: (MachineryOwner, Boolean) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Banner info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Hyper-Local Marketplace Match",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (lastExtraction != null) {
                        "Showing equipment matching extracted coordinates: Machinery: ${lastExtraction.machineryType ?: "All"}, Location: ${lastExtraction.location ?: "All Area"}"
                    } else {
                        "Showing all nearby heavy machinery owners around Amravati, MH. Fill request on Workspace to hyper-filter."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Marketplace Listings (${matchedOwners.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (matchedOwners.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SentimentDissatisfied,
                        contentDescription = "No matches",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No matching operators found in this area yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(matchedOwners) { owner ->
                    val availability = registeredAvailabilities.find { av ->
                        av.ownerId == owner.id && 
                        (av.machineryType?.equals(owner.machineryType, ignoreCase = true) == true ||
                         (av.machineryType?.contains("Harvester", ignoreCase = true) == true && owner.machineryType.contains("Harvester", ignoreCase = true)))
                    }
                    val isInitiallyBooked = mockBookings.any { it.title.startsWith(owner.modelName) }
                    OwnerCard(
                        owner = owner,
                        availability = availability,
                        isInitiallyBooked = isInitiallyBooked,
                        onBookStatusChanged = { isBooked ->
                            onBookOwner(owner, isBooked)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OwnerCard(
    owner: MachineryOwner,
    availability: AgriShiftExtraction? = null,
    isInitiallyBooked: Boolean = false,
    onBookStatusChanged: (Boolean) -> Unit = {}
) {
    var bookedState by remember(isInitiallyBooked) { mutableStateOf(isInitiallyBooked) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("owner_card_${owner.id}"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = owner.ownerName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PinDrop,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = owner.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                // Rating
                Row(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = owner.rating.toString(),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (availability != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.OfflinePin,
                            contentDescription = "Prioritized Match",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Prioritized Match • Available Now",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Dates: ${availability.availableDates?.joinToString(", ") ?: "Anytime"} • Hours: ${availability.availableHours ?: "9AM-6PM"} • Radius: ${availability.locationRadius?.toInt() ?: 15}km",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = owner.machineryType,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = owner.modelName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${owner.ratePerAcreInr.toInt()} / Acre",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Approx ₹3,000 baseline",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Outlined phone button
                OutlinedButton(
                    onClick = {
                        // Phone dialer trigger feedback
                    },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Call Operator")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call Owner", fontSize = 12.sp)
                }

                // Book machinery button
                Button(
                    onClick = {
                        val newState = !bookedState
                        bookedState = newState
                        onBookStatusChanged(newState)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (bookedState) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
                        contentColor = if (bookedState) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(
                        imageVector = if (bookedState) Icons.Default.CheckCircle else Icons.Default.CalendarToday,
                        contentDescription = "Book"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (bookedState) "Booked!" else "Confirm Booking", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun HistoryTab(
    history: List<AgriShiftEntity>,
    mockBookings: List<ProfileBooking>,
    onRateBooking: (ProfileBooking) -> Unit = {},
    onItemClick: (AgriShiftEntity) -> Unit
) {
    var selectedHistoryType by remember { mutableStateOf(0) } // 0: Bookings, 1: AI Translation Logs

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "AgriShift History Panel",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Custom filter pills / segment control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedHistoryType == 0) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { selectedHistoryType = 0 }
                    .padding(vertical = 10.dp)
                    .testTag("history_type_bookings"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = if (selectedHistoryType == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "My Bookings (${mockBookings.size})",
                        color = if (selectedHistoryType == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedHistoryType == 1) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { selectedHistoryType = 1 }
                    .padding(vertical = 10.dp)
                    .testTag("history_type_ai_logs"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = if (selectedHistoryType == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "AI Translation (${history.size})",
                        color = if (selectedHistoryType == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedHistoryType == 0) {
            // Bookings Section
            if (mockBookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = "Empty Bookings",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You don't have any active or past bookings yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mockBookings) { booking ->
                        BookingHistoryItemCard(booking = booking, onRateBooking = onRateBooking)
                    }
                }
            }
        } else {
            // AI Translation Logs Section
            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = "Empty History",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No history records found in Room database yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(history) { entity ->
                        HistoryItemCard(entity = entity, onClick = onItemClick)
                    }
                }
            }
        }
    }
}

@Composable
fun StarRatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    starSize: androidx.compose.ui.unit.Dp = 32.dp,
    maxStars: Int = 5
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            Icon(
                imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Star $i",
                tint = if (isSelected) Color(0xFFFBC02D) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(starSize)
                    .clickable { onRatingChanged(i.toFloat()) }
                    .testTag("star_${i}")
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentalReviewDialog(
    booking: ProfileBooking,
    onDismissRequest: () -> Unit,
    onSubmitReview: (equipmentQuality: Float, operatorPerformance: Float, reviewText: String) -> Unit
) {
    var equipmentQuality by remember { mutableStateOf(0f) }
    var operatorPerformance by remember { mutableStateOf(0f) }
    var reviewText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Rate & Review Rental",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = booking.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Divider()

                // Equipment Quality
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Equipment Quality",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "How was the condition and quality of the machinery?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StarRatingBar(
                        rating = equipmentQuality,
                        onRatingChanged = { equipmentQuality = it },
                        modifier = Modifier.testTag("rating_equipment_quality")
                    )
                }

                // Operator Performance
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Operator Performance",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Was the operator punctual, skilled, and professional?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StarRatingBar(
                        rating = operatorPerformance,
                        onRatingChanged = { operatorPerformance = it },
                        modifier = Modifier.testTag("rating_operator_performance")
                    )
                }

                Divider()

                // Written review
                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Write your feedback (optional)") },
                    placeholder = { Text("E.g., The operator was very professional and completed the harvest on time. Highly recommended!") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("review_text_field"),
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )

                if (showError) {
                    Text(
                        text = "Please provide ratings for both equipment and operator.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (equipmentQuality == 0f || operatorPerformance == 0f) {
                        showError = true
                    } else {
                        onSubmitReview(equipmentQuality, operatorPerformance, reviewText)
                    }
                },
                modifier = Modifier.testTag("submit_review_button")
            ) {
                Text("Submit Review")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag("cancel_review_button")
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun BookingHistoryItemCard(
    booking: ProfileBooking,
    onRateBooking: (ProfileBooking) -> Unit = {}
) {
    var showReviewDialog by remember { mutableStateOf(false) }

    if (showReviewDialog) {
        RentalReviewDialog(
            booking = booking,
            onDismissRequest = { showReviewDialog = false },
            onSubmitReview = { eq, op, text ->
                val avg = (eq + op) / 2f
                onRateBooking(
                    booking.copy(
                        rating = avg,
                        reviewText = text,
                        equipmentQualityRating = eq,
                        operatorPerformanceRating = op
                    )
                )
                showReviewDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_history_card_${booking.title.replace(" ", "_")}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if (booking.status == "Upcoming") MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color(0xFFE8F5E9),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (booking.status == "Upcoming") Icons.Default.Agriculture else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (booking.status == "Upcoming") MaterialTheme.colorScheme.primary else Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = booking.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = booking.date,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                text = booking.cost,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = if (booking.status == "Upcoming") MaterialTheme.colorScheme.primaryContainer else Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = booking.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (booking.status == "Upcoming") MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Sub-component for Rating & Reviews (Completed only)
            if (booking.status == "Completed") {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(12.dp))

                if (booking.rating == null) {
                    // Prompt to rate & review
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Build trust! Rate your experience.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }

                        Button(
                            onClick = { showReviewDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("rate_review_btn_${booking.title.replace(" ", "_")}")
                        ) {
                            Text(
                                text = "Rate & Review",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Display submitted rating & review details
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Overall:",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFBC02D),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = String.format(Locale.US, "%.1f", booking.rating),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Show ratings breakdown
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                booking.equipmentQualityRating?.let { eq ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Agriculture,
                                            contentDescription = "Equipment Rating",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "Equip: ⭐${eq.toInt()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                booking.operatorPerformanceRating?.let { op ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Operator Rating",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "Op: ⭐${op.toInt()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        if (!booking.reviewText.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Comment,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Feedback Details:",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = booking.reviewText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    entity: AgriShiftEntity,
    onClick: (AgriShiftEntity) -> Unit
) {
    val date = remember(entity.timestamp) {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        sdf.format(Date(entity.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(entity) }
            .testTag("history_item_${entity.id}"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when (entity.workflow) {
                                    "CALCULATIONS" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                    "VOICE_TO_COMMAND" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (entity.workflow) {
                                "CALCULATIONS" -> Icons.Default.Calculate
                                "VOICE_TO_COMMAND" -> Icons.Default.Mic
                                else -> Icons.Default.Agriculture
                            },
                            contentDescription = "Workflow type",
                            tint = when (entity.workflow) {
                                "CALCULATIONS" -> MaterialTheme.colorScheme.secondary
                                "VOICE_TO_COMMAND" -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = entity.workflow,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\"${entity.rawInput}\"",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location badge
                if (entity.location != null) {
                    Text(
                        text = "📍 ${entity.location}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                // Land size badge
                if (entity.landSizeAcres != null) {
                    Text(
                        text = "📐 ${entity.landSizeAcres} ac",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                // Language Tag
                if (entity.originalLanguage != null) {
                    Text(
                        text = "🗣️ ${entity.originalLanguage}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

data class ProfileBooking(
    val title: String,
    val date: String,
    val cost: String,
    val status: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val rating: Float? = null,
    val reviewText: String? = null,
    val equipmentQualityRating: Float? = null,
    val operatorPerformanceRating: Float? = null
)

data class ProfileMachine(
    val modelName: String,
    val machineryType: String,
    val hp: String,
    val year: String,
    val rate: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDialog(
    profileName: String,
    onProfileNameChange: (String) -> Unit,
    profileBusiness: String,
    onProfileBusinessChange: (String) -> Unit,
    profilePhone: String,
    onProfilePhoneChange: (String) -> Unit,
    profileEmail: String,
    onProfileEmailChange: (String) -> Unit,
    profileLocationVillage: String,
    onProfileLocationVillageChange: (String) -> Unit,
    profileLocationDistrict: String,
    onProfileLocationDistrictChange: (String) -> Unit,
    profileLocationState: String,
    onProfileLocationStateChange: (String) -> Unit,
    isOwnerMode: Boolean,
    onIsOwnerModeChange: (Boolean) -> Unit,
    renterLandSize: String,
    onRenterLandSizeChange: (String) -> Unit,
    renterCrops: String,
    onRenterCropsChange: (String) -> Unit,
    ownerHpValue: String,
    onOwnerHpValueChange: (String) -> Unit,
    ownerModelYear: String,
    onOwnerModelYearChange: (String) -> Unit,
    ownerOperatorIncluded: Boolean,
    onOwnerOperatorIncludedChange: (Boolean) -> Unit,
    walletBalance: Double,
    onWalletBalanceChange: (Double) -> Unit,
    bankAccountNo: String,
    onBankAccountNoChange: (String) -> Unit,
    bankUpiId: String,
    onBankUpiIdChange: (String) -> Unit,
    kycVerified: Boolean,
    onKycVerifiedChange: (Boolean) -> Unit,
    kycDocType: String,
    onKycDocTypeChange: (String) -> Unit,
    mockBookings: List<ProfileBooking>,
    favoriteMachines: List<String>,
    calendarBlockedDays: List<Int>,
    myMachineryFleet: List<ProfileMachine>,
    onDismiss: () -> Unit
) {
    var showAddFleetDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Unified Profile Panel",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Profile")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mode switcher toggle
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Select Application Mode",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (!isOwnerMode) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { onIsOwnerModeChange(false) }
                                    .padding(vertical = 8.dp)
                                    .testTag("mode_toggle_renter"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Agriculture,
                                        contentDescription = null,
                                        tint = if (!isOwnerMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Renter (Farmer)",
                                        color = if (!isOwnerMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isOwnerMode) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { onIsOwnerModeChange(true) }
                                    .padding(vertical = 8.dp)
                                    .testTag("mode_toggle_owner"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Business,
                                        contentDescription = null,
                                        tint = if (isOwnerMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Owner (Supplier)",
                                        color = if (isOwnerMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }

                // Avatar and Basic Details
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = if (profileName.isNotBlank()) {
                            profileName.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString("").uppercase()
                        } else "RP"
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        IconButton(
                            onClick = { 
                                android.widget.Toast.makeText(context, "Avatar editing is available soon!", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = profileName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isOwnerMode) "Heavy Equipment Supplier" else "Regional Agriculturalist",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Section: Basic Details
                Text(
                    text = "Basic Information",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = profileName,
                    onValueChange = onProfileNameChange,
                    label = { Text("Full Name / Owner Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("profile_edit_name"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = profileBusiness,
                    onValueChange = onProfileBusinessChange,
                    label = { Text("Business / Center Name") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("profile_edit_business"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = profilePhone,
                    onValueChange = onProfilePhoneChange,
                    label = { Text("Verified Mobile Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    trailingIcon = {
                        Row(
                            modifier = Modifier.padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = "Verified Status", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Text("Verified", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("profile_edit_phone"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = profileEmail,
                    onValueChange = onProfileEmailChange,
                    label = { Text("Email Address (Optional)") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().testTag("profile_edit_email"),
                    singleLine = true
                )

                Text(
                    text = "Operating / Primary Location",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = profileLocationVillage,
                        onValueChange = onProfileLocationVillageChange,
                        label = { Text("Village/Town") },
                        modifier = Modifier.weight(1f).testTag("profile_edit_village"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = profileLocationDistrict,
                        onValueChange = onProfileLocationDistrictChange,
                        label = { Text("District") },
                        modifier = Modifier.weight(1f).testTag("profile_edit_district"),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = profileLocationState,
                    onValueChange = onProfileLocationStateChange,
                    label = { Text("State") },
                    modifier = Modifier.fillMaxWidth().testTag("profile_edit_state"),
                    singleLine = true
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Render Role-Specific Fields
                if (!isOwnerMode) {
                    // Farmer (Renter) Settings
                    Text(
                        text = "Farmer Mode & Land Details",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = renterLandSize,
                        onValueChange = onRenterLandSizeChange,
                        label = { Text("Land Size (Acres)") },
                        leadingIcon = { Icon(Icons.Default.Landscape, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("profile_edit_land_size"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = renterCrops,
                        onValueChange = onRenterCropsChange,
                        label = { Text("Primary Crop Types") },
                        placeholder = { Text("e.g. Soybean, Wheat, Cotton") },
                        leadingIcon = { Icon(Icons.Default.Eco, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("profile_edit_crops"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your Saved / Favorite Machines",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (favoriteMachines.isEmpty()) {
                        Text("No favorite machines listed.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            favoriteMachines.forEach { fav ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Favorite, contentDescription = "Fav", tint = Color.Red, modifier = Modifier.size(16.dp))
                                            Text(fav, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                        }
                                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Booking History Logs",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        mockBookings.forEach { booking ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(booking.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        Text("${booking.date} • ${booking.cost}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (booking.status == "Upcoming") MaterialTheme.colorScheme.primaryContainer else Color(0xFFE8F5E9),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = booking.status,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (booking.status == "Upcoming") MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                } else {
                    // Owner (Supplier) Settings
                    Text(
                        text = "Manage Fleet (My Machinery)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        myMachineryFleet.forEach { machine ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.Agriculture, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                            Text(machine.modelName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        }
                                        Text("${machine.machineryType} • ${machine.hp} • Year ${machine.year}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Price: ${machine.rate}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(machine.status, style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { showAddFleetDialog = true },
                            modifier = Modifier.fillMaxWidth().testTag("add_to_fleet_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Machine to My Fleet")
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Availability Calendar",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap on dates to toggle between Booked/Blocked (Amber) and Available (Green)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("July 2026", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            
                            val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                daysOfWeek.forEach { day ->
                                    Text(day, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                                }
                            }
                            
                            for (row in 0..3) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    for (col in 1..7) {
                                        val dayNumber = row * 7 + col
                                        val isBlocked = calendarBlockedDays.contains(dayNumber)
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(if (isBlocked) WarningYellow else Color(0xFF4CAF50))
                                                .clickable {
                                                    if (isBlocked) {
                                                        (calendarBlockedDays as MutableList<Int>).remove(dayNumber)
                                                    } else {
                                                        (calendarBlockedDays as MutableList<Int>).add(dayNumber)
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$dayNumber",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Earnings Dashboard",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Total Revenue", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("₹42,500", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Pending Payments", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                Text("₹5,000", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Operator/Driver Details", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text("Does machinery include professional operator?", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = ownerOperatorIncluded,
                                onCheckedChange = onOwnerOperatorIncludedChange,
                                modifier = Modifier.testTag("operator_included_switch")
                            )
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Section: Trust, Security & Logistics
                Text(
                    text = "Trust, Security & Logistics",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Government KYC Verification", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("Doc: $kycDocType", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                                Text("Verified", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Your Service Rating", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("Based on 24 regional bookings", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(Icons.Default.Star, contentDescription = "Rating Star", tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                            Text("4.8", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Digital Wallet Balance", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text("₹$walletBalance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                            Button(
                                onClick = {
                                    android.widget.Toast.makeText(context, "Payout of ₹$walletBalance initiated to $bankAccountNo!", android.widget.Toast.LENGTH_LONG).show()
                                    onWalletBalanceChange(0.0)
                                },
                                enabled = walletBalance > 0,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Withdraw")
                            }
                        }
                        
                        OutlinedTextField(
                            value = bankAccountNo,
                            onValueChange = onBankAccountNoChange,
                            label = { Text("Bank Account Number") },
                            leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = bankUpiId,
                            onValueChange = onBankUpiIdChange,
                            label = { Text("UPI ID for Payouts") },
                            leadingIcon = { Icon(Icons.Default.Payment, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Help & Support", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    android.widget.Toast.makeText(context, "Calling AgriShift Support helpline: 1800-419-8888...", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call Support")
                            }
                            Button(
                                onClick = {
                                    android.widget.Toast.makeText(context, "Support Live Chat initialized!", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat Support")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("profile_save_button")
            ) {
                Text("Save & Close")
            }
        }
    )

    if (showAddFleetDialog) {
        var newModel by remember { mutableStateOf("") }
        var newType by remember { mutableStateOf("Tractor") }
        var newHp by remember { mutableStateOf("50 HP") }
        var newYear by remember { mutableStateOf("2024") }
        var newRate by remember { mutableStateOf("₹1,500/hr") }
        
        AlertDialog(
            onDismissRequest = { showAddFleetDialog = false },
            title = { Text("Add Fleet Machinery") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newModel,
                        onValueChange = { newModel = it },
                        label = { Text("Model Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newType,
                        onValueChange = { newType = it },
                        label = { Text("Machinery Type") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newHp,
                        onValueChange = { newHp = it },
                        label = { Text("Horsepower (HP)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newYear,
                        onValueChange = { newYear = it },
                        label = { Text("Model Year") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newRate,
                        onValueChange = { newRate = it },
                        label = { Text("Rental Price Rate") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newModel.isNotBlank()) {
                            (myMachineryFleet as MutableList<ProfileMachine>).add(
                                ProfileMachine(newModel, newType, newHp, newYear, newRate, "Active")
                            )
                        }
                        showAddFleetDialog = false
                    }
                ) {
                    Text("Add Machine")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFleetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

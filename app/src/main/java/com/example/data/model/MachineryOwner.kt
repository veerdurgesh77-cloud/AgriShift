package com.example.data.model

data class MachineryOwner(
    val id: Int,
    val ownerName: String,
    val machineryType: String,
    val modelName: String,
    val ratePerAcreInr: Double,
    val location: String,
    val rating: Float,
    val phoneNumber: String
)

object MachineryData {
    val sampleOwners = listOf(
        MachineryOwner(
            id = 1,
            ownerName = "Ramesh Patil",
            machineryType = "Combine Harvester",
            modelName = "John Deere W70",
            ratePerAcreInr = 3000.0,
            location = "Amravati, MH",
            rating = 4.8f,
            phoneNumber = "+91 98765 43210"
        ),
        MachineryOwner(
            id = 2,
            ownerName = "Sanjay Pawar",
            machineryType = "Combine Harvester",
            modelName = "Class Crop Tiger 30",
            ratePerAcreInr = 2900.0,
            location = "Pune, MH",
            rating = 4.7f,
            phoneNumber = "+91 91234 56789"
        ),
        MachineryOwner(
            id = 3,
            ownerName = "Gurpreet Singh",
            machineryType = "Combine Harvester",
            modelName = "Kartar 4000",
            ratePerAcreInr = 3100.0,
            location = "Satara, MH",
            rating = 4.9f,
            phoneNumber = "+91 94455 66778"
        ),
        MachineryOwner(
            id = 4,
            ownerName = "Vijay Deshmukh",
            machineryType = "Tractor",
            modelName = "Mahindra Arjun Ultra 1",
            ratePerAcreInr = 1300.0,
            location = "Nashik, MH",
            rating = 4.6f,
            phoneNumber = "+91 95566 77889"
        ),
        MachineryOwner(
            id = 5,
            ownerName = "Anil Shinde",
            machineryType = "Rotavator",
            modelName = "Maschio Gaspardo",
            ratePerAcreInr = 1600.0,
            location = "Pune, MH",
            rating = 4.5f,
            phoneNumber = "+91 92233 44556"
        ),
        MachineryOwner(
            id = 6,
            ownerName = "Ramsevak Yadav",
            machineryType = "Tractor",
            modelName = "Swaraj 744 FE",
            ratePerAcreInr = 1200.0,
            location = "Patna, BR",
            rating = 4.4f,
            phoneNumber = "+91 88877 66554"
        ),
        MachineryOwner(
            id = 7,
            ownerName = "Devendra Joshi",
            machineryType = "Plough",
            modelName = "Lemken Opal 90",
            ratePerAcreInr = 1300.0,
            location = "Indore, MP",
            rating = 4.8f,
            phoneNumber = "+91 77766 55443"
        )
    )
}

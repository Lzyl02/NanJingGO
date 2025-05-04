package com.COMP3040.NanjingGo.Adapter

import com.google.firebase.database.FirebaseDatabase

// Data class to represent ticket details for a location
data class Ticket(
    val adult: String,
    val discounted: String? = null,
    val doubleTicket: String? = null,
    val extremeFlyingBall: String? = null,
    val animalPlanetVR: String? = null,
    val nightAdult: String? = null,
    val nightDiscounted: String? = null
)

// Data class to represent a location with its details and associated ticket information
data class Location(
    val name: String,
    val address: String,
    val phone1: String,
    val phone2: String? = null,
    val website: String,
    val description: String,
    val openingTime: String,
    val rating: String,
    val suggestedDuration: String,
    val bestSeason: String,
    val ticket: Ticket
)

// Function to upload a list of predefined locations to Firebase Realtime Database
fun uploadLocationsToFirebase() {
    // Reference to the Firebase database "locations" node
    val database = FirebaseDatabase.getInstance().getReference("locations")

    // List of locations with detailed information
    val locations = listOf(
        Location(
            name = "Qixia Mountain",
            address = "No. 88 Qixia Street, Qixia District, Nanjing, China",
            phone1 = "025-85766979",
            phone2 = "025-85761831",
            website = "www.njqixiashan.com",
            description = "Qixia Mountain, also known as She Mountain, is located approximately 22 kilometers northeast of downtown Nanjing in Qixia Town. Famous for its vibrant red maple leaves, it hosts an annual Red Maple Festival each autumn, attracting many visitors with its stunning scenery. The mountain features three peaks, with the highest reaching 313 meters, and is home to historic landmarks such as Qixia Temple and Sheli Tower. Natural attractions include Mingjing Lake, known for its picturesque pavilion and nine-curve bridge, as well as unique rock formations like Die Lang Yan ('Overlapping Waves Rock'). Additionally, the mountain offers panoramic views of the Yangtze River from Hushan Summit and showcases remnants of Emperor Qianlong’s visits, adding rich cultural heritage to its natural beauty.",
            openingTime = "All year 08:00-16:00",
            rating = "4.6",
            suggestedDuration = "2 to 3 hours",
            bestSeason = "Autumn",
            ticket = Ticket(
                adult = "¥23 and up",
                discounted = "¥12 and up",
                doubleTicket = "¥48.1 and up"
            )
        ),
        // Additional locations with similar structure...
        Location(
            name = "Zhongshan Mountain National Park",
            address = "No. 88 Qixia Street, Qixia District, Nanjing, China",
            phone1 = "025-85766979",
            phone2 = "025-85761831",
            website = "www.njqixiashan.com",
            description = "Zhongshan Scenic Area, also known as Zijin Mountain, is a prominent mountain near downtown Nanjing, standing at 448.9 meters. It features key attractions such as the Sun Yat-sen Mausoleum, the Ming Xiaoling Mausoleum, and Linggu Temple. The area is renowned for its beautiful tree-lined paths, historic landmarks, and scenic views of the Yangtze River. Visitors can explore significant cultural sites, enjoy peaceful hikes, and experience the rich heritage that makes Zhongshan one of Nanjing’s most celebrated destinations.",
            openingTime = "All year, open all day",
            rating = "4.6",
            suggestedDuration = "3 to 6 days",
            bestSeason = "Spring, Autumn",
            ticket = Ticket(
                adult = "¥60 and up",
                discounted = "¥35 and up"
            )
        )
    )

    // Start uploading the locations, with index starting from 5
    var index = 5

    // Iterate through each location in the list
    locations.forEach { location ->
        // Save the location data under a unique index in the database
        database.child(index.toString()).setValue(location)
            .addOnSuccessListener {
                // Log success message
                println("Successfully uploaded location at index $index: ${location.name}")
            }
            .addOnFailureListener { e ->
                // Log failure message with error details
                println("Failed to upload location at index $index: ${location.name}. Error: ${e.message}")
            }
        index++ // Increment index for the next location
    }
}

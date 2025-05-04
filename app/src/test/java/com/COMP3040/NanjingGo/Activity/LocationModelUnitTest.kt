package com.COMP3040.NanjingGO.Activity

import android.os.Parcel
import com.COMP3040.NanjingGo.Domain.LocationModel
import com.COMP3040.NanjingGo.Domain.TicketModel
import org.junit.Test
import org.junit.Assert.*
import org.robolectric.annotation.Config
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

/**
 * Unit tests for the `LocationModel` class.
 *
 * These tests verify the functionality and behavior of the `LocationModel` class,
 * including Parcelable implementation, default values, object equality, and handling
 * of null optional fields.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28]) // Specify the SDK version to use for Robolectric
class LocationModelUnitTest {

    /**
     * Tests the Parcelable implementation of `LocationModel`.
     *
     * This test creates a `LocationModel` object, writes it to a `Parcel`,
     * reads it back, and verifies that the original and recreated objects are equal.
     */
    @Test
    fun testParcelableLocationModel() {
        // Create a `LocationModel` instance
        val location = LocationModel(
            name = "Test Location",
            address = "Test Address",
            phone = "123456789",
            website = "http://test.com",
            description = "Test Description",
            openingTime = "9:00 AM - 5:00 PM",
            rating = 4.5,
            suggestedDuration = "2 hours",
            bestSeason = "summer",
            picture = "http://test.com/image.jpg",
            ticket = TicketModel("Group guide", "Digital tour", "Private guide", "None"),
            travelTips = listOf("Tip 1", "Tip 2"),
            isFavorite = true
        )

        // Write to Parcel
        val parcel = Parcel.obtain()
        location.writeToParcel(parcel, 0)
        parcel.setDataPosition(0) // Reset parcel for reading

        // Read from Parcel
        val recreatedLocation = LocationModel.CREATOR.createFromParcel(parcel)

        // Assert equality
        assertEquals(location, recreatedLocation)

        // Recycle the parcel
        parcel.recycle()
    }

    /**
     * Tests the default values of the `LocationModel` class.
     *
     * Verifies that the default constructor initializes all fields with their expected default values.
     */
    @Test
    fun testDefaultValues() {
        val location = LocationModel()

        // Verify default values
        assertEquals("", location.name)
        assertEquals("", location.address)
        assertEquals("", location.phone)
        assertNull(location.website)
        assertEquals("", location.description)
        assertEquals("", location.openingTime)
        assertEquals(0.0, location.rating, 0.0)
        assertEquals("", location.suggestedDuration)
        assertEquals("", location.bestSeason)
        assertNull(location.picture)
        assertNull(location.ticket)
        assertEquals(listOf<String>(), location.travelTips)
        assertFalse(location.isFavorite)
    }

    /**
     * Tests the equality of `LocationModel` objects.
     *
     * Verifies that two objects with identical data are considered equal, and
     * that modifying a field makes them unequal.
     */
    @Test
    fun testEquality() {
        val location1 = LocationModel(
            name = "Test Location",
            address = "Test Address",
            phone = "123456789",
            website = "http://test.com",
            description = "Test Description",
            openingTime = "9:00 AM - 5:00 PM",
            rating = 4.5,
            suggestedDuration = "2 hours",
            bestSeason = "summer",
            picture = "http://test.com/image.jpg",
            ticket = TicketModel("Group guide", "Digital tour", "Private guide", "None"),
            travelTips = listOf("Tip 1", "Tip 2"),
            isFavorite = true
        )

        val location2 = LocationModel(
            name = "Test Location",
            address = "Test Address",
            phone = "123456789",
            website = "http://test.com",
            description = "Test Description",
            openingTime = "9:00 AM - 5:00 PM",
            rating = 4.5,
            suggestedDuration = "2 hours",
            bestSeason = "summer",
            picture = "http://test.com/image.jpg",
            ticket = TicketModel("Group guide", "Digital tour", "Private guide", "None"),
            travelTips = listOf("Tip 1", "Tip 2"),
            isFavorite = true
        )

        // Assert equality
        assertEquals(location1, location2)

        // Modify one property and check inequality
        val location3 = location2.copy(name = "New Location")
        assertNotEquals(location1, location3)
    }

    /**
     * Tests handling of null values for optional fields in `LocationModel`.
     *
     * Verifies that optional fields such as `website`, `picture`, and `ticket`
     * can be set to null without causing errors.
     */
    @Test
    fun testNullOptionalFields() {
        val location = LocationModel(
            name = "Test Location",
            address = "Test Address",
            phone = "123456789",
            website = null, // Optional field
            description = "Test Description",
            openingTime = "9:00 AM - 5:00 PM",
            rating = 4.5,
            suggestedDuration = "2 hours",
            bestSeason = "summer",
            picture = null, // Optional field
            ticket = null,  // Optional field
            travelTips = listOf(),
            isFavorite = false
        )

        // Assert null values for optional fields
        assertNull(location.website)
        assertNull(location.picture)
        assertNull(location.ticket)
    }
}

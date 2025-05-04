package com.COMP3040.NanjingGO.Activity

import android.os.Parcel
import com.COMP3040.NanjingGo.Domain.LocationModel
import com.COMP3040.NanjingGo.Domain.User
import org.junit.Test
import org.junit.Assert.*
import org.robolectric.annotation.Config
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

/**
 * Unit tests for the User class.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28]) // Specify the SDK version Robolectric should emulate
class UserUnitTest {

    /**
     * Test that the User class implements Parcelable correctly.
     *
     * This test ensures that a User object can be written to and read back from a Parcel
     * without any data loss or corruption.
     */
    @Test
    fun testParcelableUser() {
        // Create a LocationModel instance to add to the user's favorite locations
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
            ticket = null,
            travelTips = listOf("Tip 1", "Tip 2"),
            isFavorite = true
        )

        // Create a User instance with the location in favorites
        val user = User(
            username = "testUser",
            email = "test@example.com",
            favoriteLocations = listOf(location)
        )

        // Write the User object to a Parcel
        val parcel = Parcel.obtain()
        user.writeToParcel(parcel, 0)
        parcel.setDataPosition(0) // Reset parcel for reading

        // Read the User object back from the Parcel
        val recreatedUser = User.CREATOR.createFromParcel(parcel)

        // Assert that the original and recreated User objects are equal
        assertEquals(user, recreatedUser)

        // Recycle the parcel to free resources
        parcel.recycle()
    }

    /**
     * Test the default values of the User class.
     *
     * This test ensures that a User object created without any arguments
     * has the expected default values for all properties.
     */
    @Test
    fun testUserDefaultValues() {
        val user = User()

        // Verify default values
        assertEquals("", user.username)
        assertEquals("", user.email)
        assertTrue(user.favoriteLocations.isEmpty())
    }

    /**
     * Test the creation of a User object with non-default values.
     *
     * This test ensures that a User object can be created with specified
     * values for all properties and that these values are correctly assigned.
     */
    @Test
    fun testUserWithNonDefaultValues() {
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
            ticket = null,
            travelTips = listOf("Tip 1", "Tip 2"),
            isFavorite = true
        )

        // Create a User object with specified values
        val user = User(
            username = "testUser",
            email = "test@example.com",
            favoriteLocations = listOf(location)
        )

        // Verify non-default values
        assertEquals("testUser", user.username)
        assertEquals("test@example.com", user.email)
        assertEquals(1, user.favoriteLocations.size)
        assertEquals("Test Location", user.favoriteLocations[0].name)
    }

    /**
     * Test the equality of User objects.
     *
     * This test verifies that two User objects with identical properties
     * are considered equal and that modifying one property results in inequality.
     */
    @Test
    fun testEquality() {
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
            ticket = null,
            travelTips = listOf("Tip 1", "Tip 2"),
            isFavorite = true
        )

        // Create two User objects with identical properties
        val user1 = User(
            username = "testUser",
            email = "test@example.com",
            favoriteLocations = listOf(location)
        )

        val user2 = User(
            username = "testUser",
            email = "test@example.com",
            favoriteLocations = listOf(location)
        )

        // Verify that the two User objects are equal
        assertEquals(user1, user2)

        // Modify one property and verify inequality
        val user3 = user2.copy(username = "newUser")
        assertNotEquals(user1, user3)
    }
}

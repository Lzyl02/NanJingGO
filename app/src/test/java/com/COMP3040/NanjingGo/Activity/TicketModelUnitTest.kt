package com.COMP3040.NanjingGO.Activity

import android.os.Parcel
import com.COMP3040.NanjingGo.Domain.TicketModel
import org.junit.Test
import org.junit.Assert.*
import org.robolectric.annotation.Config
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

/**
 * Unit tests for the TicketModel class.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28]) // Specify the SDK version for Robolectric
class TicketModelUnitTest {

    /**
     * Test that the TicketModel class correctly implements Parcelable.
     * Verifies that an instance can be written to and restored from a Parcel.
     */
    @Test
    fun testParcelableImplementation() {
        // Create a TicketModel instance with test data
        val ticket = TicketModel(
            groupGuideService = "Group guide",
            digitalTour = "Digital tour",
            privateGuideService = "Private guide",
            other = "None"
        )

        // Write the object to a Parcel
        val parcel = Parcel.obtain()
        ticket.writeToParcel(parcel, 0) // Writing the object
        parcel.setDataPosition(0) // Reset the Parcel position for reading

        // Recreate the object from the Parcel
        val recreatedTicket = TicketModel.CREATOR.createFromParcel(parcel)

        // Assert the original and recreated objects are equal
        assertEquals(ticket, recreatedTicket)

        // Assert individual fields match
        assertEquals(ticket.groupGuideService, recreatedTicket.groupGuideService)
        assertEquals(ticket.digitalTour, recreatedTicket.digitalTour)
        assertEquals(ticket.privateGuideService, recreatedTicket.privateGuideService)
        assertEquals(ticket.other, recreatedTicket.other)

        // Recycle the Parcel
        parcel.recycle()
    }

    /**
     * Test the TicketModel's field values.
     * Verifies that the values are correctly assigned and retrievable.
     */
    @Test
    fun testTicketModelFields() {
        // Create a TicketModel instance
        val ticket = TicketModel(
            groupGuideService = "Group guide",
            digitalTour = "Digital tour",
            privateGuideService = "Private guide",
            other = "None"
        )

        // Assert each field holds the expected value
        assertEquals("Group guide", ticket.groupGuideService)
        assertEquals("Digital tour", ticket.digitalTour)
        assertEquals("Private guide", ticket.privateGuideService)
        assertEquals("None", ticket.other)
    }
}

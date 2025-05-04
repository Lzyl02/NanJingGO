package com.COMP3040.NanjingGo.Domain

import android.os.Parcel
import android.os.Parcelable

/**
 * Data class representing a location with various attributes.
 *
 * @property name Name of the location.
 * @property address Address of the location.
 * @property phone Contact phone number.
 * @property website Website URL of the location (optional).
 * @property description Detailed description of the location.
 * @property openingTime Operating hours of the location.
 * @property rating Average rating of the location.
 * @property suggestedDuration Recommended visit duration.
 * @property bestSeason The best season to visit.
 * @property picture URL of a picture representing the location (optional).
 * @property ticket TicketModel object containing ticket-related information (optional).
 * @property travelTips List of travel tips for the location.
 * @property isFavorite Indicates if the location is marked as a favorite.
 */
data class LocationModel(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val website: String? = null,
    val description: String = "",
    val openingTime: String = "",
    val rating: Double = 0.0,
    val suggestedDuration: String = "",
    val bestSeason: String = "",
    val picture: String? = null,
    val ticket: TicketModel? = null,
    val travelTips: List<String> = listOf(),
    val isFavorite: Boolean = false
) : Parcelable {
    // Constructor to create LocationModel from a Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readDouble(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString(),
        parcel.readParcelable(TicketModel::class.java.classLoader),
        mutableListOf<String>().apply { parcel.readStringList(this) },
        parcel.readByte() != 0.toByte()
    )

    // Write LocationModel data to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(phone)
        parcel.writeString(website)
        parcel.writeString(description)
        parcel.writeString(openingTime)
        parcel.writeDouble(rating)
        parcel.writeString(suggestedDuration)
        parcel.writeString(bestSeason)
        parcel.writeString(picture)
        parcel.writeParcelable(ticket, flags)
        parcel.writeStringList(travelTips)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    // Describe the contents (no special file descriptors used)
    override fun describeContents(): Int {
        return 0
    }

    // Parcelable Creator to create LocationModel from a Parcel
    companion object CREATOR : Parcelable.Creator<LocationModel> {
        override fun createFromParcel(parcel: Parcel): LocationModel {
            return LocationModel(parcel)
        }

        override fun newArray(size: Int): Array<LocationModel?> {
            return arrayOfNulls(size)
        }
    }
}

/**
 * Data class representing ticket details for a location.
 *
 * @property groupGuideService Group guide service information (optional).
 * @property digitalTour Digital tour information (optional).
 * @property privateGuideService Private guide service information (optional).
 * @property other Additional ticket-related information (optional).
 */
data class TicketModel(
    val groupGuideService: String? = null,
    val digitalTour: String? = null,
    val privateGuideService: String? = null,
    val other: String? = null
) : Parcelable {
    // Constructor to create TicketModel from a Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    // Write TicketModel data to a Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(groupGuideService)
        parcel.writeString(digitalTour)
        parcel.writeString(privateGuideService)
        parcel.writeString(other)
    }

    // Describe the contents (no special file descriptors used)
    override fun describeContents(): Int {
        return 0
    }

    // Parcelable Creator to create TicketModel from a Parcel
    companion object CREATOR : Parcelable.Creator<TicketModel> {
        override fun createFromParcel(parcel: Parcel): TicketModel {
            return TicketModel(parcel)
        }

        override fun newArray(size: Int): Array<TicketModel?> {
            return arrayOfNulls(size)
        }
    }
}

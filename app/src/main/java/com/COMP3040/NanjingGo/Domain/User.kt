package com.COMP3040.NanjingGo.Domain

import android.os.Parcel
import android.os.Parcelable

/**
 * Data class representing a user.
 *
 * @property username The username of the user.
 * @property email The email address of the user.
 * @property favoriteLocations A list of the user's favorite locations.
 */
data class User(
    val username: String = "",
    val email: String = "",
    val favoriteLocations: List<LocationModel> = emptyList() // List of favorite locations
) : Parcelable {

    /**
     * Constructor to create a User object from a Parcel.
     *
     * @param parcel The Parcel containing the serialized User data.
     */
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(), // Read username
        parcel.readString().orEmpty(), // Read email
        parcel.createTypedArrayList(LocationModel.CREATOR) ?: emptyList() // Deserialize the list of LocationModel
    )

    /**
     * Writes the User object's data to a Parcel.
     *
     * @param parcel The Parcel to write to.
     * @param flags Additional flags about how the object should be written.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username) // Serialize username
        parcel.writeString(email) // Serialize email
        parcel.writeTypedList(favoriteLocations) // Serialize the list of LocationModel
    }

    /**
     * Describe the contents of the Parcelable object.
     *
     * @return A bitmask indicating the set of special object types marshaled by this Parcelable.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Companion object for creating User objects from a Parcel or creating an array of User objects.
     */
    companion object CREATOR : Parcelable.Creator<User> {
        /**
         * Creates a User object from a Parcel.
         *
         * @param parcel The Parcel containing the serialized User data.
         * @return A new User object.
         */
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        /**
         * Creates an array of User objects.
         *
         * @param size The size of the array to create.
         * @return An array of User objects.
         */
        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}

package com.COMP3040.NanjingGo.Activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.COMP3040.NanjingGo.R
import com.google.firebase.database.*

/**
 * Activity for displaying location-specific tips.
 *
 * This activity retrieves tips from Firebase Realtime Database for a specific location
 * and dynamically displays them in a structured format. Tips are categorized as titles,
 * sub-points, or paragraphs based on the provided formatting.
 */
class TipsActivity : AppCompatActivity() {

    /**
     * Firebase database reference for accessing location data.
     */
    private val databaseReference = FirebaseDatabase.getInstance().getReference("locations")

    /**
     * Called when the activity is created.
     *
     * Initializes the activity and loads tips for a specific location from Firebase.
     *
     * @param savedInstanceState If the activity is being reinitialized after being
     * previously shut down, this Bundle contains the data it most recently supplied.
     * Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tips)

        // Load tips from Firebase database
        loadTipsFromFirebase()
    }

    /**
     * Retrieves tips for a specified location from the Firebase database.
     *
     * The location name is retrieved from the Intent that started the activity.
     * The tips are displayed dynamically in the UI after retrieval and parsing.
     */
    private fun loadTipsFromFirebase() {
        val locationName = intent.getStringExtra("locationName")
        if (locationName.isNullOrEmpty()) {
            Toast.makeText(this, "Unable to retrieve location name", Toast.LENGTH_SHORT).show()
            return
        }

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (locationSnapshot in snapshot.children) {
                    val name = locationSnapshot.child("name").getValue(String::class.java)
                    if (name == locationName) {
                        val tipsData = locationSnapshot.child("tips").getValue(String::class.java)
                        if (!tipsData.isNullOrEmpty()) {
                            val parsedData = parseTipsData(tipsData)
                            displayParsedTips(parsedData)
                        } else {
                            showErrorMessage("Tips data is empty")
                        }
                        return
                    }
                }
                showErrorMessage("No tips found for the specified location")
            }

            override fun onCancelled(error: DatabaseError) {
                showErrorMessage("Failed to load tips: ${error.message}")
            }
        })
    }

    /**
     * Parses the raw tips data from Firebase into a structured format.
     *
     * @param rawData Raw string containing tips data with formatting markers.
     * @return A list of pairs where each pair contains the text and a boolean
     * indicating whether it is a title.
     */
    private fun parseTipsData(rawData: String): List<Pair<String, Boolean>> {
        val lines = rawData.split("\\\\n".toRegex())
        val parsedData = mutableListOf<Pair<String, Boolean>>()

        lines.forEach { line ->
            val trimmedLine = line.trim()
            when {
                trimmedLine.isEmpty() -> { /* Skip empty lines */ }
                trimmedLine.endsWith(":") -> parsedData.add(trimmedLine to true) // Title
                trimmedLine.startsWith("-") -> parsedData.add(trimmedLine to false) // Sub-point
                else -> parsedData.add(trimmedLine to false) // Paragraph
            }
        }
        return parsedData
    }

    /**
     * Dynamically displays the parsed tips data in a linear layout.
     *
     * @param data List of parsed tips, each represented as a pair of text and a boolean
     * indicating if it is a title.
     */
    private fun displayParsedTips(data: List<Pair<String, Boolean>>) {
        val container = findViewById<LinearLayout>(R.id.contentLayout)

        data.forEachIndexed { index, (text, isTitle) ->
            val textView = TextView(this).apply {
                this.text = text
                textSize = if (isTitle) 20f else 16f
                setTextColor(if (isTitle) Color.BLACK else Color.DKGRAY)
                setTypeface(null, if (isTitle) Typeface.BOLD else Typeface.NORMAL)
                setPadding(8, 16, 8, 16)
                if (!isTitle) setLineSpacing(1.5f, 1.2f)
            }
            container.addView(textView)

            if (index < data.size - 1) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2
                    ).apply {
                        setMargins(0, 8, 0, 8)
                    }
                    setBackgroundColor(Color.LTGRAY)
                }
                container.addView(divider)
            }
        }
    }

    /**
     * Displays an error message in the UI when tips data cannot be loaded.
     *
     * @param message The error message to display.
     */
    private fun showErrorMessage(message: String) {
        val container = findViewById<LinearLayout>(R.id.contentLayout)
        val textView = TextView(this).apply {
            text = message
            textSize = 16f
            setTextColor(Color.RED)
            setPadding(8, 16, 8, 16)
        }
        container.addView(textView)
    }
}

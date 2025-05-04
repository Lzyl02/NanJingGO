package com.COMP3040.NanjingGo.Activity

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

/**
 * A base activity class for other activities in the application to inherit from.
 * This class provides common functionality, such as setting the layout to extend
 * to the edges of the screen.
 */
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set flags to extend the layout to the edges of the screen,
        // allowing for immersive experiences.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}
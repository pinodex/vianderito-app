package com.raphaelmarco.vianderito

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.roundToInt

class WelcomeActivity : AppCompatActivity() {

    private lateinit var btnGetStarted: Button

    private lateinit var viewGetStarted: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        btnGetStarted = findViewById<Button>(R.id.button_get_started)
        viewGetStarted = findViewById<View>(R.id.view_get_started)

        btnGetStarted.setOnClickListener {
            //var intent = Intent(baseContext, GetStartedActivity::class.java)

            //startActivity(intent)

            val cx = (btnGetStarted.x + btnGetStarted.width / 2).roundToInt()
            val cy = (btnGetStarted.y + btnGetStarted.height / 2).roundToInt()

            // get the final radius for the clipping circle
            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

            // create the animator for this view (the start radius is zero)
            val anim = ViewAnimationUtils.createCircularReveal(viewGetStarted, cx, cy,
                    0f, finalRadius).apply {
                interpolator = AccelerateDecelerateInterpolator()
                duration = 500
            }

            // make the view visible and start the animation
            viewGetStarted.visibility = View.VISIBLE

            anim.start()
        }
    }
}

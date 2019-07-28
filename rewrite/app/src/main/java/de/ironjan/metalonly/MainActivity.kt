package de.ironjan.metalonly

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private var isPlaying: Boolean = false

    private lateinit var action_play: Drawable
    private lateinit var action_stop: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        action_play = ResourcesCompat.getDrawable(resources, android.R.drawable.ic_media_pause, theme)!!
        action_stop = ResourcesCompat.getDrawable(resources, android.R.drawable.ic_media_pause, theme)!!

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            togglePlaying()
        }
    }

    private fun togglePlaying() {
        if(isPlaying) {
            fab.setImageDrawable(action_play)

            Snackbar.make(fab, "Stopped playing", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            isPlaying = false
        }else {
            fab.setImageDrawable(action_stop)

            Snackbar.make(fab, "Started playing", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            isPlaying = true
        }
    }
}
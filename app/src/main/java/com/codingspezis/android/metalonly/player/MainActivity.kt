package com.codingspezis.android.metalonly.player

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView
    private lateinit var action_play_stop: MenuItem

    var isPlaying: Boolean = false

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_wish -> {
                textMessage.setText(R.string.title_wishes)
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_play_stop -> {
                if(isPlaying) {
                    textMessage.setText("Stopped")
                    action_play_stop.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_action_play, theme)
                }else{
                    textMessage.setText("Playing")
                    action_play_stop.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_action_stop, theme)
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_plan -> {
                textMessage.setText(R.string.title_plan)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        textMessage = findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        action_play_stop = navView.menu.findItem(R.id.action_play_stop)
    }
}

package com.codingspezis.android.metalonly.player

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var textMessage: TextView
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_wish -> {
                textMessage.setText(R.string.title_wishes)
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_play_stop -> {
                textMessage.setText(R.string.title_play)
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
    }
}

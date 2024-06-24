package com.aichixiguaj.heartprogress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val heartProgress = findViewById<HeartProgress>(R.id.heartProgress)

        lifecycleScope.launch {
            while (true) {
                delay(1000)
                val intRange = 0..300
                val random = intRange.random()
                withContext(Dispatchers.Main) {
                    heartProgress.setCurrentHeartRate(random)
                }
            }
        }
    }
}
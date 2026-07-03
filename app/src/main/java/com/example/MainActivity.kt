package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.AgriShiftApp
import com.example.ui.AgriShiftViewModel
import com.example.ui.AgriShiftViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: AgriShiftViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this, AgriShiftViewModelFactory(applicationContext))[AgriShiftViewModel::class.java]
        
        setContent {
            MyApplicationTheme {
                AgriShiftApp(viewModel = viewModel)
            }
        }
    }
}

package com.cleansound.cleansound.controller

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.cleansound.cleansound.R
import com.cleansound.cleansound.databinding.ActivityLibraryBinding

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflar el layout con ViewBinding
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botón volver
        binding.btnBack.setOnClickListener {
            finish() // vuelve a MainActivity
        }
    }
}
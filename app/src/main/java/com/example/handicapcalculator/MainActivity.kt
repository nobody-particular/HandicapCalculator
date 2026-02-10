package com.example.handicapcalculator

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.handicapcalculator.classes.Player
import com.example.handicapcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var players = mutableListOf<Player>()
    // private lateinit var adapter: PlayerAdapter
    private lateinit var binding: ActivityMainBinding
    //private val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Save a player to the database
    private fun savePlayer(player: Player) {
    }

    // Delete a player from the database
    private fun deletePlayer(player: Player) {
    }
}
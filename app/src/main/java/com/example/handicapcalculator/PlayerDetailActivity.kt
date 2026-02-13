package com.example.handicapcalculator

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.handicapcalculator.database.AppDatabase
import com.example.handicapcalculator.databinding.ActivityPlayerDetailBinding
import kotlinx.coroutines.launch

// Activity for displaying the game list for a player
class PlayerDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityPlayerDetailBinding.inflate(layoutInflater)

        val database = AppDatabase.getDatabase(this)

        val playerId = intent.getIntExtra("PLAYER_ID", -1)

        lifecycleScope.launch {
            val player = database.playerDao().getPlayerById(playerId)

            player?.let {
                binding.titleText.text = it.name
            }
        }
    }
}
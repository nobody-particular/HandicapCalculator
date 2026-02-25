package com.example.handicapcalculator

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.handicapcalculator.classes.Player
import com.example.handicapcalculator.database.AppDatabase
import com.example.handicapcalculator.databinding.ActivityMainBinding
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
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(this)

        val playerId = intent.getIntExtra("PLAYER_ID", -1)

        lifecycleScope.launch {
            val player = database.playerDao().getPlayerById(playerId)

            player?.let {
                binding.titleText.text = it.name
                binding.handicapText.text =
                    getString(R.string.course_handicap_text, it.calculateCourseHandicap().toString())
                binding.indexText.text =
                    getString(R.string.handicap_index_text, it.calculateHandicapIndex().toString())
            }
        }

        binding.buttonDelete.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("PLAYER_ID", playerId)

            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
package com.example.handicapcalculator

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.handicapcalculator.adapters.GameAdapter
import com.example.handicapcalculator.adapters.PlayerAdapter
import com.example.handicapcalculator.classes.Game
import com.example.handicapcalculator.classes.Player
import com.example.handicapcalculator.database.AppDatabase
import com.example.handicapcalculator.databinding.ActivityMainBinding
import com.example.handicapcalculator.databinding.ActivityPlayerDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Activity for displaying the game list for a player
class PlayerDetailActivity : AppCompatActivity() {
    private lateinit var adapter: GameAdapter
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
                    getString(
                        R.string.course_handicap_text,
                        it.calculateCourseHandicap().toString()
                    )
                binding.indexText.text =
                    getString(R.string.handicap_index_text, it.calculateHandicapIndex().toString())
            }

            // Create the GameAdapter object and detail what happens when a game is clicked
            adapter = GameAdapter(player?.games ?: mutableListOf()) { clickedGame ->
                player?.games?.remove(clickedGame)
            }

            // Set the recyclerView adapter to the GameAdapter
            binding.recyclerView.adapter = adapter
        }

        // Set the layoutManager to be linear
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.buttonDelete.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("PLAYER_ID", playerId)

            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // Create a launcher for adding a game
        val addGameLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val date = result.data?.getStringExtra("GAME_DATE") ?: ""
                    val score = result.data?.getIntExtra("GAME_SCORE", 0) ?: 0

                    val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


                    val newGame = Game(
                        try {
                            format.parse(date)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        } ?: Date(), score
                    )

                    val player = database.playerDao().getPlayerById(playerId)
                    player?.games?.add(newGame)
                }
            }

        // Launch the launcher when the floating action button is pressed
        binding.floatingActionButtonAddGame.setOnClickListener {
            val intent = Intent(this, AddGameActivity::class.java)
            addGameLauncher.launch(intent)
        }
    }
}
package com.example.handicapcalculator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
    private var player: Player? = null

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
            player = database.playerDao().getPlayerById(playerId)

            player?.let {
                binding.titleText.text = it.name
                updateNumbers()

                // Create the GameAdapter object and detail what happens when a game is clicked
                adapter = GameAdapter(player!!.games, player!!) { clickedGame ->
                    AlertDialog.Builder(this@PlayerDetailActivity)
                        .setTitle("Delete Game")
                        .setMessage("Are you sure you want to delete this game?")
                        .setPositiveButton("Yes") { dialog, which ->
                            val index = player?.games?.indexOf(clickedGame) ?: -1

                            // Remove the game
                            player?.games?.remove(clickedGame)

                            // Notify adapter of the game removed
                            adapter.notifyItemRemoved(index)

                            // Update player in database
                            lifecycleScope.launch {
                                player?.let { database.playerDao().updatePlayer(it) }

                                updateNumbers()
                            }

                            // Notify adapter that all other games may have changed
                            adapter.notifyItemRangeChanged(0, player!!.games.size)
                        }
                        .setNegativeButton("No", null)
                        .show()
                }

                // Set the recyclerView adapter to the GameAdapter
                binding.recyclerView.adapter = adapter
            }
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

                    val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

                    val newGame = Game(
                        try {
                            format.parse(date)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        } ?: Date(), score
                    )

                    player?.games?.let {
                        it.add(newGame)
                        adapter.notifyItemInserted(it.size + 1)
                    }

                    lifecycleScope.launch {
                        player?.let {
                            database.playerDao().updatePlayer(it)
                        }
                    }

                    updateNumbers()

                    // Notify adapter that all other games may have changed
                    adapter.notifyItemRangeChanged(0, player!!.games.size)
                }
            }

        // Launch the launcher when the floating action button is pressed
        binding.floatingActionButtonAddGame.setOnClickListener {
            val intent = Intent(this, AddGameActivity::class.java)
            addGameLauncher.launch(intent)
        }
    }

    // Update the numbers shown in the title
    fun updateNumbers() {
        player?.let {
            binding.handicapText.text =
                getString(
                    R.string.course_handicap_text,
                    it.calculateCourseHandicap().toString()
                )
            binding.indexText.text =
                getString(
                    R.string.handicap_index_text,
                    String.format(Locale.getDefault(), "%.1f", it.calculateHandicapIndex())
                )
        }
    }
}
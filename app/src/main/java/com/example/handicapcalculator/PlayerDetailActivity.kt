package com.example.handicapcalculator

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
import com.example.handicapcalculator.classes.Game
import com.example.handicapcalculator.classes.Player
import com.example.handicapcalculator.database.AppDatabase
import com.example.handicapcalculator.databinding.ActivityPlayerDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Activity for displaying the game list for a player
class PlayerDetailActivity : AppCompatActivity() {
    // An adapter to connect the mutableList to the recyclerView
    private lateinit var adapter: GameAdapter

    // The binding for the activity, which references all the UI elements
    private lateinit var binding: ActivityPlayerDetailBinding

    // The player whose details are being shown
    private var player: Player? = null

    // What should happen when the activity is started
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
            // Get the player from the database
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

        // What should happen when the "Delete" button is clicked
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

                    // Try to make a game with the parsed date
                    // If the parse is unsuccessful, then set date to today
                    val game = Game(
                        try {
                            format.parse(date)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        } ?: Date(), score
                    )

                    player?.games?.let {
                        // Add the game and sort
                        it.add(0, game)

                        it.sortDescending()

                        // Notify the adapter of the added game
                        adapter.notifyItemInserted(it.indexOf(game))

                        // Remove the last game if the size is more than 20
                        if (it.size > 20) {
                            val lastIndex = it.size - 1

                            it.removeAt(lastIndex)

                            adapter.notifyItemRemoved(lastIndex)
                        }
                    }

                    // Update the player in the database
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
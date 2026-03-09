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
import com.example.handicapcalculator.adapters.PlayerAdapter
import com.example.handicapcalculator.classes.Player
import com.example.handicapcalculator.database.AppDatabase
import com.example.handicapcalculator.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

// Activity for displaying the player list
class MainActivity : AppCompatActivity() {
    private var players = mutableListOf<Player>()
    private lateinit var adapter: PlayerAdapter
    private lateinit var binding: ActivityMainBinding
    private val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create a launcher for deleting a player
        val deletePlayerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val id = result.data?.getIntExtra("PLAYER_ID", -1) ?: -1
            if (result.resultCode == RESULT_OK) {
                if (id != -1) {
                    AlertDialog.Builder(this)
                        .setTitle("Delete Player")
                        .setMessage("Are you sure you want to delete this player?")
                        .setPositiveButton("Yes") { dialog, which ->
                            val player = players.find { it.id == id }
                            player?.let {
                                deletePlayer(it)
                            }
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            } else {
                val index = players.indexOf(players.find { it.id == id })

                if (index >= 0) {
                    adapter.notifyItemChanged(index)
                }
            }
        }

        // Create the PlayerAdapter object and detail what happens when a player is clicked
        adapter = PlayerAdapter(players) { clickedPlayer ->
            val intent = Intent(this, PlayerDetailActivity::class.java)
            intent.putExtra("PLAYER_ID", clickedPlayer.id)
            deletePlayerLauncher.launch(intent)
        }

        // Set the recyclerView adapter to the PlayerAdapter
        binding.recyclerView.adapter = adapter
        // Set the layoutManager to be linear
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            // Fetch all players from Room
            val playersFromDatabase = database.playerDao().getAllPlayers().toMutableList().apply { sort() }

            // Update the local list and notify adapter
            players.clear()
            players.addAll(playersFromDatabase)

            adapter.notifyDataSetChanged()
        }

        // Create a launcher for adding a player
        val addPlayerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val name = result.data?.getStringExtra("PLAYER_NAME") ?: ""

                val id = (players.maxOfOrNull { it.id } ?: 0) + 1

                // Create the new player and save it to the database
                val newPlayer = Player(id, name = name)
                savePlayer(newPlayer)
            }
        }

        // Launch the launcher when the floating action button is pressed
        binding.floatingActionButtonAddPlayer.setOnClickListener {
            val intent = Intent(this, AddPlayerActivity::class.java)
            addPlayerLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            // Fetch all players from Room
            val playersFromDatabase = database.playerDao().getAllPlayers().toMutableList().apply { sort() }

            // Update the local list and notify adapter
            players.clear()
            players.addAll(playersFromDatabase)

            adapter.notifyDataSetChanged()
        }
    }

    // Save a player to the database
    private fun savePlayer(player: Player) {
        lifecycleScope.launch {
            // Insert player into database
            database.playerDao().insertPlayer(player)

            // Insert player into players
            players.add(player)

            players.sort()

            val index = players.size + 1

            // Notify adapter of the player inserted
            adapter.notifyItemInserted(index)
        }
    }

    // Delete a player from the database
    private fun deletePlayer(player: Player) {
        lifecycleScope.launch {
            // Remove player from database
            database.playerDao().deletePlayer(player)

            val index = players.indexOf(player)

            // Remove player from players
            players.remove(player)

            // Notify adapter of the player removed
            adapter.notifyItemRemoved(index)
        }
    }
}
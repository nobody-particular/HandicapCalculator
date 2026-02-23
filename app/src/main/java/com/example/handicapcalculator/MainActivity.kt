package com.example.handicapcalculator

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.handicapcalculator.adapters.PlayerAdapter
import com.example.handicapcalculator.classes.Game
import com.example.handicapcalculator.classes.Player
import com.example.handicapcalculator.database.AppDatabase
import com.example.handicapcalculator.databinding.ActivityMainBinding
import com.example.handicapcalculator.databinding.ActivityPlayerDetailBinding

// Activity for displaying the player list
class MainActivity : AppCompatActivity() {
    private var players = mutableListOf<Player>(Player(1, "Joe", mutableListOf<Game>()))
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

        // Create the PlayerAdapter object and detail what happens when a player is clicked
        adapter = PlayerAdapter(players) { clickedPlayer ->
            val intent = Intent(this, PlayerDetailActivity::class.java)
            intent.putExtra("PLAYER_ID", clickedPlayer.id)
            startActivity(intent)
        }

        // Set the recyclerView adapter to the PlayerAdapter
        binding.recyclerView.adapter = adapter
        // Set the layoutManager to be linear
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Create a launcher for adding a player
        val addPlayerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val name = result.data?.getStringExtra("PLAYER_NAME") ?: ""

                // Create the new player and save it to the database
                val newPlayer = Player(name = name)
                savePlayer(newPlayer)
            }
        }

        // Launch the launcher when the floating action button is pressed
        binding.floatingActionButtonAdd.setOnClickListener {
            val intent = Intent(this, AddPlayerActivity::class.java)
            addPlayerLauncher.launch(intent)
        }
    }

    // Save a player to the database
    private fun savePlayer(player: Player) {
    }

    // Delete a player from the database
    private fun deletePlayer(player: Player) {
    }
}
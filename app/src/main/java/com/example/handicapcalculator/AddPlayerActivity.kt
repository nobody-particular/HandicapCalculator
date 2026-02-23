package com.example.handicapcalculator

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.handicapcalculator.databinding.ActivityAddPlayerBinding
import com.example.handicapcalculator.databinding.ActivityMainBinding

// Activity for adding a player
class AddPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonSave.setOnClickListener {
            val name = binding.editTextName.text.toString()

            if (name.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("PLAYER_NAME", name)

                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}
package com.example.handicapcalculator

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.handicapcalculator.databinding.ActivityAddGameBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Activity for adding a game
class AddGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityAddGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editTextDate.setOnClickListener {
            // Get current date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Create and show the DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // The month value is 0-indexed, so add 1 for display
                    val selectedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                    binding.editTextDate.setText(selectedDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        binding.buttonSave.setOnClickListener {
            val date = getDateFromEditText()
            val score = binding.editTextScore.text.toString().toIntOrNull()

            if (date != null && score != null) {
                val resultIntent = Intent()
                resultIntent.putExtra("GAME_DATE", date)
                resultIntent.putExtra("GAME_SCORE", score)

                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    fun getDateFromEditText(): Date? {
        val dateString = binding.editTextDate.text.toString()
        if (dateString.isEmpty()) return null

        // Use the same format used when setting the text
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return try {
            format.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
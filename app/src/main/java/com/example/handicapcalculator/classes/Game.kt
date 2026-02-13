package com.example.handicapcalculator.classes

import android.os.Build
import androidx.room.Entity
import java.util.Date

// Class to represent a game
@Entity(tableName = "games")
data class Game(
    val date: Date,
    val score: Int
) : Comparable<Game> {
    // Compare by date
    override fun compareTo(other: Game): Int {
        return date.compareTo(other.date)
    }

    // Calculate the handicap differential using constants from Constants.kt
    fun calculateHandicapDifferential() : Double {
        return (score - COURSE_RATING) * (SLOPE_CONSTANT / SLOPE_RATING)
    }
}
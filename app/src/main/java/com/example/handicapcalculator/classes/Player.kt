package com.example.handicapcalculator.classes

import androidx.room.Entity
import androidx.room.PrimaryKey

// Class to represent a player
@Entity(tableName = "players")
class Player(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val games: MutableList<Game>  = mutableListOf<Game>(),
    val bestGames: MutableList<Game> = mutableListOf<Game>() // The 8 games with the lowest adjusted gross score
) : Comparable<Player> {
    // Compare by name
    override fun compareTo(other: Player): Int {
        return name.compareTo(other.name)
    }

    // Place the 8 games with the lowest adjusted gross score in bestGames
    fun findBestGames() {
        for (game in games) {
            if (bestGames.size < 8) {
                bestGames.add(game)
            } else {
                val worstOfBestGames = bestGames.maxBy { it.score }
                if (game.score < worstOfBestGames.score) {
                    bestGames.remove(worstOfBestGames)
                    bestGames.add(game)
                }
            }

        }
    }

    // Calculate the handicap index with the 8 games with the lowest adjusted gross score
    fun calculateHandicapIndex() : Double {
        findBestGames()
        val sum = bestGames.sumOf { it.calculateHandicapDifferential() }
        return sum / bestGames.size
    }

    // Find the course handicap with the handicap index
    fun calculateCourseHandicap() : Int {
        val handicapIndex = calculateHandicapIndex()
        for ((min, handicap) in courseHandicapMap) {
            if (handicapIndex < min) {
                return handicap - 1
            }
        }
        return courseHandicapMap.maxBy { it.value }.value
    }
}
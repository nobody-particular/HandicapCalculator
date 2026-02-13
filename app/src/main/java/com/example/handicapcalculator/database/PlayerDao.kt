package com.example.handicapcalculator.database

import androidx.room.Dao
import androidx.room.Query
import com.example.handicapcalculator.classes.Player

// Interface as a Data Access Object (DAO) for defining how to access players from the database
@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayerById(playerId: Int): Player?
}
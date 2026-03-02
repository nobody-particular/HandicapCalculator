package com.example.handicapcalculator.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.handicapcalculator.classes.Player

// Interface as a Data Access Object (DAO) for defining how to access, insert, update, and delete players from the database
@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayerById(playerId: Int): Player?

    @Query("SELECT * FROM players")
    suspend fun getAllPlayers(): List<Player>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player)

    @Update
    suspend fun updatePlayer(vararg players: Player): Int

    @Delete
    suspend fun deletePlayer(vararg players: Player)
}
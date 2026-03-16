package com.example.handicapcalculator.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.handicapcalculator.R
import com.example.handicapcalculator.classes.Game
import com.example.handicapcalculator.classes.Player
import java.text.SimpleDateFormat
import java.util.Locale

// Class to connect the game list to the recyclerView in the PlayerDetailActivity
class GameAdapter(
    private val games: List<Game>,
    private val player: Player,
    private val onClick: (Game) -> Unit
) : RecyclerView.Adapter<GameAdapter.ViewHolder>() {
    // Constructor for the viewHolder
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.playerName)
        val scoreText: TextView = view.findViewById(R.id.playerHandicap)
        val differentialText: TextView = view.findViewById(R.id.playerIndex)
    }

    // What should happen when the viewHolder is started
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.player_item, parent, false)
        return ViewHolder(view)
    }

    // What should happen when the viewHolder is bound to the recyclerView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = games[position]
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        holder.dateText.text = formatter.format(game.date)
        holder.differentialText.text = String.format(Locale.getDefault(), "%.1f", game.calculateHandicapDifferential())
        holder.scoreText.text = String.format("%s%s", game.score.toString(), if (game in player.bestGames) "*" else " ")
        holder.itemView.setOnClickListener { onClick(game) }
    }

    override fun getItemCount(): Int {
        return games.size
    }
}
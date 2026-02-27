package com.example.handicapcalculator.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.handicapcalculator.R
import com.example.handicapcalculator.classes.Game

class GameAdapter(
    private val games: List<Game>,
    private val onClick: (Game) -> Unit
) : RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.playerName)
        val scoreText: TextView = view.findViewById(R.id.playerHandicap)
        val differentialText: TextView = view.findViewById(R.id.playerIndex)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.player_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = games[position]
        holder.dateText.text = game.date.toString()
        holder.differentialText.text = game.calculateHandicapDifferential().toString()
        holder.scoreText.text = game.score.toString()
        holder.itemView.setOnClickListener { onClick(game) }
    }

    override fun getItemCount(): Int {
        return games.size
    }
}
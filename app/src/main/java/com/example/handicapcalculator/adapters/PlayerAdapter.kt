package com.example.handicapcalculator.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.handicapcalculator.classes.Player
import com.example.handicapcalculator.R
import java.util.Locale

// Class to connect the player list to the recyclerView in the MainActivity
class PlayerAdapter (
    private val players: List<Player>,
    private val onClick: (Player) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.playerName)
        val handicapText: TextView = view.findViewById(R.id.playerHandicap)
        val indexText: TextView = view.findViewById(R.id.playerIndex)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.player_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = players[position]
        holder.nameText.text = player.name
        holder.indexText.text = String.format(Locale.getDefault(), "%.1f", player.calculateHandicapIndex())
        holder.handicapText.text = player.calculateCourseHandicap().toString()
        holder.itemView.setOnClickListener { onClick(player) }
    }

    override fun getItemCount(): Int {
        return players.size
    }
}
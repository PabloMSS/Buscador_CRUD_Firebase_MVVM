package com.example.buscador_cruceros.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.buscador_cruceros.Models.Crucero
import com.example.buscador_cruceros.R
import com.example.buscador_cruceros.ViewHolder.CruceroViewHolder

class CruceroAdapter(private val deleteCruise: (Int) -> Unit): ListAdapter<Crucero, CruceroViewHolder>(MiElementoDiffCallback())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CruceroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_cruceros, parent, false)
        return CruceroViewHolder(view)
    }

    override fun onBindViewHolder(holder: CruceroViewHolder, position: Int) {
        holder.render(getItem(position))
        holder.itemView.findViewById<ImageView>(R.id.imgDelete).setOnClickListener {
            deleteCruise(position)
        }
    }

    class MiElementoDiffCallback : DiffUtil.ItemCallback<Crucero>(){
        override fun areItemsTheSame(oldItem: Crucero, newItem: Crucero): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crucero, newItem: Crucero): Boolean {
            return oldItem == newItem
        }

    }
}
package com.example.buscador_cruceros.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.buscador_cruceros.Models.Crucero
import com.example.buscador_cruceros.R
import com.example.buscador_cruceros.ViewHolder.CruceroViewHolder

class CruceroAdapter(private val cruceros: List<Crucero>): RecyclerView.Adapter<CruceroViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CruceroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_cruceros, parent, false)
        return CruceroViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cruceros.size
    }

    override fun onBindViewHolder(holder: CruceroViewHolder, position: Int) {
        holder.render(cruceros[position])
    }

}
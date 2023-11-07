package com.example.buscador_cruceros.Adapter

import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.buscador_cruceros.EditCrucero
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

        var deleteIcon = holder.itemView.findViewById<ImageView>(R.id.imgDelete)
        var editIcon = holder.itemView.findViewById<ImageView>(R.id.imgEdit)

        var dialog = Dialog(deleteIcon.context)
        dialog.setContentView(R.layout.dialog_confirm)
        var tvPregunta = dialog.findViewById<TextView>(R.id.tv_pregunta_confirmacion)
        var btnNo = dialog.findViewById<Button>(R.id.btn_no)
        var btnSi = dialog.findViewById<Button>(R.id.btn_si)

        deleteIcon.setOnClickListener {
            dialog.show()
            tvPregunta.text = dialog.context.getString(R.string.preguntaDelete)

            btnNo.setOnClickListener {
                dialog.hide()
            }

            btnSi.setOnClickListener {
                deleteCruise(position)
                dialog.hide()
            }
        }

        editIcon.setOnClickListener {
            var crucero: Crucero = getItem(position)
            val intent = Intent(editIcon.context, EditCrucero::class.java)
            intent.putExtra("editCrucero", crucero)
            editIcon.context.startActivity(intent)
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
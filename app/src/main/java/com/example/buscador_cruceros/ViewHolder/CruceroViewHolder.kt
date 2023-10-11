package com.example.buscador_cruceros.ViewHolder

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buscador_cruceros.DetallesActivity
import com.example.buscador_cruceros.Models.Crucero
import com.example.buscador_cruceros.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class CruceroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var db = Firebase.firestore
    val storage = Firebase.storage

    private var imgCrucero = view.findViewById<ImageView>(R.id.imgCrucero)
    private var tvTitle = view.findViewById<TextView>(R.id.tvTitle)
    private var tvDescripcion = view.findViewById<TextView>(R.id.tvDescripcion)
    private var cvCrucero = view.findViewById<CardView>(R.id.cvCrucero)

    fun render(crucero: Crucero){
        tvTitle.text = crucero.name
        tvDescripcion.text = crucero.infoDescription
        getImg(crucero.image.toString())

        cvCrucero.setOnClickListener {
            val intent = Intent(cvCrucero.context, DetallesActivity::class.java)
            intent.putExtra("crucero", crucero)
            cvCrucero.context.startActivity(intent)
        }

    }

    fun getImg(id: String){
        val referenciaImagen = storage.reference.child("cruceros/${id}.png")
        referenciaImagen.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(imgCrucero.context).load(uri).into(imgCrucero)
        }.addOnFailureListener {
            Log.i("IMG NOT WORKING", it.toString())
        }
    }

}
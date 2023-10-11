package com.example.buscador_cruceros

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buscador_cruceros.Adapter.CruceroAdapter
import com.example.buscador_cruceros.Models.Crucero
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class BuscadorCrucero : AppCompatActivity() {

    var db = Firebase.firestore
    val storage = Firebase.storage

    var listCruceros = mutableListOf<Crucero>()

    private lateinit var tvTitle: TextView
    private lateinit var svCrucero: SearchView
    private lateinit var rvCrucero: RecyclerView
    private lateinit var btnFloating: FloatingActionButton

    private lateinit var cruceroAdapter: CruceroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscador_crucero)
        initComponent()
        initUI()
    }

    fun initComponent(){
        tvTitle = findViewById(R.id.tvTitle)
        svCrucero = findViewById(R.id.svCrucero)
        rvCrucero = findViewById(R.id.rvCrucero)
        btnFloating = findViewById(R.id.btnFloating)
    }

    fun initUI(){
        cruceroAdapter = CruceroAdapter(listCruceros)
        rvCrucero.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvCrucero.adapter = cruceroAdapter
        getAll()
        goAddCruise()
    }

    fun getAll(){
        db.collection("crucero")
            .get()
            .addOnSuccessListener {
                for(item in it){
                    var crucero : Crucero = item.toObject(Crucero::class.java)
                    listCruceros.add(crucero)
                }
                actualizarAdapter()
            }
    }

    fun goAddCruise(){
        btnFloating.setOnClickListener {
            var intent = Intent(this, AddCrucero::class.java)
            startActivity(intent)
        }
    }

    fun actualizarAdapter(){
        cruceroAdapter.notifyDataSetChanged()
    }
}
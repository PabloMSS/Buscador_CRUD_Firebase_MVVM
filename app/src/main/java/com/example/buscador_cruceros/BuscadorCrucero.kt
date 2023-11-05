package com.example.buscador_cruceros

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buscador_cruceros.Adapter.CruceroAdapter
import com.example.buscador_cruceros.Models.Crucero
import com.example.buscador_cruceros.ViewModel.BuscadorViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class BuscadorCrucero : AppCompatActivity() {

    var db = Firebase.firestore
    val storage = Firebase.storage

    lateinit var viewModel: BuscadorViewModel

    var listCruceros = mutableListOf<Crucero>()

    private lateinit var tvTitle: TextView
    private lateinit var svCrucero: SearchView
    private lateinit var rvCrucero: RecyclerView
    private lateinit var btnFloating: FloatingActionButton
    private lateinit var btnReiniciar: Button

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
        btnReiniciar = findViewById(R.id.btnReiniciar)

        btnReiniciar.visibility = View.GONE
        cruceroAdapter = CruceroAdapter(){ position -> deleteCrucero(position)}
        rvCrucero.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvCrucero.adapter = cruceroAdapter
    }

    fun initUI(){
        viewModel = ViewModelProvider(this).get(BuscadorViewModel::class.java)
        viewModel.getAll()
        viewModel.listCrucerosBD.observe(this){ result ->
                listCruceros = result
                cruceroAdapter.submitList(listCruceros)
        }
        goAddCruise()
        funcionalitySearchView()

        btnReiniciar.setOnClickListener {
            filterList("")
            btnReiniciar.visibility = View.GONE
        }
    }

    fun goAddCruise(){
        btnFloating.setOnClickListener {
            var intent = Intent(this, AddCrucero::class.java)
            startActivity(intent)
        }
    }

    fun funcionalitySearchView(){
        svCrucero.setOnQueryTextListener( object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterList(query)
                btnReiniciar.visibility = View.VISIBLE
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return false
            }

        })
    }

    fun filterList(query: String?){
        if(query != null){
            listCruceros.clear()
            db.collection("crucero")
                .orderBy("yearConstruction", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    for (item in it){
                        var crucero = item.toObject<Crucero>(Crucero::class.java)
                        if(crucero.name.toString().toLowerCase().contains(query)){
                            listCruceros.add(crucero)
                        }
                    }
                    actualizarAdapter()
                }
                .addOnFailureListener {

                }
        }
    }

    fun deleteCrucero(position: Int){
        var idCrucero = listCruceros[position].id.toString()
        viewModel.deleteCrucero(idCrucero)
        viewModel.mensajeDelete.observe(this){ it ->
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.getAll()
    }

    fun actualizarAdapter(){
        cruceroAdapter.notifyDataSetChanged()
    }
}
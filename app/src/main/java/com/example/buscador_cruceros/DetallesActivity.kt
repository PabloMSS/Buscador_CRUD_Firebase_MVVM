package com.example.buscador_cruceros

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.buscador_cruceros.Models.Crucero
import com.example.buscador_cruceros.ViewModel.DetalleViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DetallesActivity : AppCompatActivity() {
    var db = Firebase.firestore
    val storage = Firebase.storage

    lateinit var viewModel: DetalleViewModel

    private lateinit var imgCruceroDetalle: ImageView
    private lateinit var tvTitleDetalle: TextView
    private lateinit var tvConstructionDetalle: TextView
    private lateinit var tvTonelajeDetalle: TextView
    private lateinit var tvCompanyDetalle: TextView
    private lateinit var tvCapacityDetalle: TextView
    private lateinit var tvCapacity2Detalle: TextView
    private lateinit var tvDescripcionDetalle: TextView
    private lateinit var btnReturnDellate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles)
        iniComponents()
        initUI()
    }

    fun iniComponents(){
        viewModel = ViewModelProvider(this).get(DetalleViewModel::class.java)
        imgCruceroDetalle = findViewById(R.id.imgCruceroDetalle)
        tvTitleDetalle = findViewById(R.id.tvTitleDetalle)
        tvConstructionDetalle = findViewById(R.id.tvConstructionDetalle)
        tvTonelajeDetalle = findViewById(R.id.tvTonelajeDetalle)
        tvCompanyDetalle = findViewById(R.id.tvCompanyDetalle)
        tvCapacityDetalle = findViewById(R.id.tvCapacityDetalle)
        tvCapacity2Detalle = findViewById(R.id.tvCapacity2Detalle)
        tvDescripcionDetalle = findViewById(R.id.tvDescripcionDetalle)
        btnReturnDellate = findViewById(R.id.btnReturnDellate)
    }

    fun initUI(){
        var crucero: Crucero = intent.extras?.getSerializable("crucero") as Crucero
        viewModel.getNaviera(crucero.company.toString())
        viewModel.naviera.observe(this){
            tvCompanyDetalle.text = tvCompanyDetalle.text.toString()+": "+it
        }
        tvTitleDetalle.text = crucero.name
        tvConstructionDetalle.text = tvConstructionDetalle.text.toString()+": "+crucero.yearConstruction
        tvTonelajeDetalle.text = tvTonelajeDetalle.text.toString()+": "+crucero.tonelaje
        tvCapacityDetalle.text = tvCapacityDetalle.text.toString()+": "+crucero.capacity
        tvCapacity2Detalle.text = tvCapacity2Detalle.text.toString()+": "+crucero.tripulantes
        getImg(crucero.id.toString())
        formatDescripcion(crucero.description.toString())
        returnActivity()
    }

    fun returnActivity(){
        btnReturnDellate.setOnClickListener {
            finish()
        }
    }

    fun formatDescripcion(descripcion: String){
        var cont = 0
        val format = StringBuilder()
        val textFormatDescripcion = descripcion.replace("\\.\\s+".toRegex(), ".")
        for (item in textFormatDescripcion){
            if(item == '.'){
                cont++
                if(cont == 2){
                    format.append(item)
                    format.append("\n\n")
                    cont = 0
                }else{
                    format.append(item+" ")
                }
            }else{
                format.append(item)
            }
        }
        tvDescripcionDetalle.text = format.toString()
    }

    fun getImg(id: String){
        val referenciaImagen = storage.reference.child("cruceros/${id}.png")
        referenciaImagen.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(imgCruceroDetalle.context).load(uri).into(imgCruceroDetalle)
        }.addOnFailureListener {
            Log.i("IMG", it.toString())
        }
    }
}
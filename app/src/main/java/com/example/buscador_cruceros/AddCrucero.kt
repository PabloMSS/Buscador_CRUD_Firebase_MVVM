package com.example.buscador_cruceros

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.*
import com.example.buscador_cruceros.Models.Crucero
import com.example.buscador_cruceros.Models.Naviera
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AddCrucero : AppCompatActivity() {
    val db = Firebase.firestore
    val storage = Firebase.storage
    val storageRef = storage.reference

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editorPreferences: SharedPreferences.Editor
    /*var sharedPreferences: SharedPreferences = getSharedPreferences("ImgPreferences", Context.MODE_PRIVATE)
    var editorPreferences: SharedPreferences.Editor = sharedPreferences.edit()*/

    var listNavieras = mutableListOf<String>()

    lateinit var etNameCruise: TextView
    lateinit var spNavieras: Spinner
    lateinit var etTonelaje: TextView
    lateinit var etPasajeros: TextView
    lateinit var etTripulantes: TextView
    lateinit var imgUpload: ImageView
    lateinit var etDescripcion: EditText
    lateinit var btnAdd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_crucero)
        initComponent()
        initUI()
    }

    fun initComponent(){
        etNameCruise = findViewById(R.id.etNameCruise)
        spNavieras = findViewById(R.id.spNavieras)
        etTonelaje = findViewById(R.id.etTonelaje)
        etPasajeros = findViewById(R.id.etPasajeros)
        etTripulantes = findViewById(R.id.etTripulantes)
        etDescripcion = findViewById(R.id.etDescripcion)
        imgUpload = findViewById(R.id.imgUpload)
        btnAdd = findViewById(R.id.btnAdd)

        var adapterSpinner = ArrayAdapter<String>(this, com.bumptech.glide.R.layout.support_simple_spinner_dropdown_item, listNavieras)
        spNavieras.adapter = adapterSpinner
        listNavieras.add(getString(R.string.infoSelect))
    }

    fun initUI(){
        createSharedPreferences()
        getAllNavieras()
        uploadImgStorage()
        getCompany()
    }

    fun createSharedPreferences(){
        sharedPreferences = getSharedPreferences("ImgPreferences", Context.MODE_PRIVATE)
        editorPreferences= sharedPreferences.edit()
    }

    fun getAllNavieras(){
        db.collection("navieras")
            .get()
            .addOnSuccessListener { result ->
                for (item in result){
                    var naviera: Naviera = item.toObject(Naviera::class.java)
                    listNavieras.add(naviera.name)
                }
            }
            .addOnFailureListener{
                Log.i("Get Navieras", it.toString())
            }
    }

    fun uploadImgStorage(){
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if(uri != null){
                imgUpload.setImageURI(uri)
                editorPreferences.putString("imgURI", uri.toString())
                editorPreferences.apply()
            }else{

            }
        }
        imgUpload.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ImageOnly))
        }
    }

    fun getCompany(){
        val adapterSpinner = ArrayAdapter(this, com.bumptech.glide.R.layout.support_simple_spinner_dropdown_item, listNavieras)
        spNavieras.adapter = adapterSpinner

        spNavieras.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val navieraSelecionada = listNavieras[position]
                db.collection("navieras")
                    .get()
                    .addOnSuccessListener {
                        for(item in it){
                            var naviera: Naviera = item.toObject(Naviera::class.java)
                            if(naviera.name.equals(navieraSelecionada)){
                                addCruise(naviera.id)
                            }
                        }
                    }
                    .addOnFailureListener {

                    }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Maneja el caso en el que no se ha seleccionado nada
            }
        }
    }

    fun addCruise(idNaviera: String){
        btnAdd.setOnClickListener {
            var nameNaviera = idNaviera
            var getUriReferences = Uri.parse(sharedPreferences.getString("imgURI", null))
            var idDocument = db.collection("crucero").document().id
            var crucero = Crucero(idDocument, etNameCruise.text.toString(), etDescripcion.text.toString(), etDescripcion.text.toString(), nameNaviera, etPasajeros.text.toString().toInt(), "2020", idDocument, etTripulantes.text.toString().toInt(), etTonelaje.text.toString().toInt())
            var imgReference = storageRef.child("cruceros/${idDocument}.png")

            db.collection("crucero")
                .document(idDocument)
                .set(crucero)
                .addOnSuccessListener {
                    imgReference.putFile(getUriReferences)
                        .addOnSuccessListener {
                            Log.i("Result", "Okey Upload Image")
                            var intent = Intent(this, BuscadorCrucero::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener{ e ->
                            Log.i("Result", "Error Upload Image")
                        }
                }
                .addOnFailureListener {

                }
        }
    }


}
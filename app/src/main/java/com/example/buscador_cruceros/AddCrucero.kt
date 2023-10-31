package com.example.buscador_cruceros

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.buscador_cruceros.Models.Crucero
import com.example.buscador_cruceros.Models.Naviera
import com.example.buscador_cruceros.ViewModel.AddViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AddCrucero : AppCompatActivity() {
    val db = Firebase.firestore
    val storage = Firebase.storage
    val storageRef = storage.reference

    lateinit var viewModel: AddViewModel

    var yearConstruction = ""
    var idNaviera = ""

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editorPreferences: SharedPreferences.Editor

    var listNavieras = mutableListOf<String>()

    lateinit var etNameCruise: TextView
    lateinit var spNavieras: AutoCompleteTextView
    lateinit var tvYearConstruction: TextView
    lateinit var etTonelaje: TextView
    lateinit var etPasajeros: TextView
    lateinit var etTripulantes: TextView
    lateinit var imgUpload: ImageView
    lateinit var etDescripcion: EditText
    lateinit var btnAdd: Button

    lateinit var yearPicker: NumberPicker
    lateinit var btnYearConfirm: Button
    lateinit var imgCalendar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_crucero)
        initComponent()
        initUI()
    }

    fun initComponent(){
        etNameCruise = findViewById(R.id.etNameCruise)
        spNavieras = findViewById(R.id.spNavieras)
        tvYearConstruction = findViewById(R.id.tvYearConstruction)
        etTonelaje = findViewById(R.id.etTonelaje)
        etPasajeros = findViewById(R.id.etPasajeros)
        etTripulantes = findViewById(R.id.etTripulantes)
        etDescripcion = findViewById(R.id.etDescripcion)
        imgUpload = findViewById(R.id.imgUpload)
        btnAdd = findViewById(R.id.btnAdd)
        imgCalendar = findViewById(R.id.imgCalendar)

        var adapterSpinner = ArrayAdapter(this, R.layout.list_item, listNavieras)
        spNavieras.setAdapter(adapterSpinner)
    }

    fun initUI(){
        viewModel = ViewModelProvider(this).get(AddViewModel::class.java)
        createSharedPreferences()
        getAllNavieras()
        uploadImgStorage()
        viewModel.idNaviera.observe(this) {
            it.let { result ->
                idNaviera = result
            }
        }

        btnAdd.setOnClickListener {
            newCrucero(idNaviera)
        }

        imgCalendar.setOnClickListener {
            fragmentYear()
        }

        val adapterSpinner = ArrayAdapter(this, R.layout.list_item , listNavieras)
        spNavieras.setAdapter(adapterSpinner)

        spNavieras.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val elementoSeleccionado = parent.getItemAtPosition(position).toString()
            viewModel.getCompanyFirebase(elementoSeleccionado)
        }
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
                Toast.makeText(this, "Debes de Seleccionar una Imagen", Toast.LENGTH_LONG).show()
            }
        }
        imgUpload.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ImageOnly))
        }
    }

    fun createInfoDescripcion(descripcion: String): String {
        var listDescripcion = descripcion.split(".")
        var infoDescripcion = listDescripcion[0]+"."
        return infoDescripcion
    }

    fun newCrucero(idNaviera: String){
            var nameNaviera = idNaviera
            var idDocument = db.collection("crucero").document().id
            var nameCrucero = etNameCruise.text.toString()
            var descripcion = etDescripcion.text.toString()
            var infoDescripcion = createInfoDescripcion(etDescripcion.text.toString())
            var pasajeros = etPasajeros.text.toString()
            var tripulantes = etTripulantes.text.toString()
            var tonelaje = etTonelaje.text.toString()
            if(idDocument.isNotEmpty() && nameCrucero.isNotEmpty() && descripcion.isNotEmpty() && infoDescripcion.isNotEmpty() && nameNaviera.isNotEmpty() && pasajeros.isNotEmpty() && tripulantes.isNotEmpty() && tonelaje.isNotEmpty() && yearConstruction != ""){
                Toast.makeText(this, "Creando Crucero", Toast.LENGTH_LONG).show()
                var crucero = Crucero(idDocument, nameCrucero, descripcion, infoDescripcion, nameNaviera, pasajeros.toInt(), yearConstruction, idDocument, tripulantes.toInt(), tonelaje.toInt())
                viewModel.addCrucero(crucero)

                var getUriReferences = Uri.parse(sharedPreferences.getString("imgURI", null))
                var imgReference = storageRef.child("cruceros/${idDocument}.png")
                imgReference.putFile(getUriReferences)
                    .addOnSuccessListener {
                        Log.i("Result", "Okey Upload Image")
                        var intent = Intent(this, BuscadorCrucero::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener{ e ->
                        Log.i("Resultttt", "Error Upload Image")
                    }
            }else{
                Toast.makeText(this, "Debes de rellenar todos los campos", Toast.LENGTH_LONG).show()
            }
    }

    @SuppressLint("NewApi")
    fun fragmentYear() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_year)
        dialog.show()

        yearPicker = dialog.findViewById(R.id.yearPicker)
        btnYearConfirm = dialog.findViewById(R.id.btnYearConfirm)
        yearPicker.textColor = ContextCompat.getColor(this, R.color.white)
        yearPicker.minValue = 1900
        yearPicker.maxValue = 2030
        yearPicker.value = 2023

        btnYearConfirm.setOnClickListener {
            dialog.hide()
            tvYearConstruction.text = getString(R.string.text_año)+": ${yearPicker.value}"
            tvYearConstruction.setTypeface(null, Typeface.NORMAL)
            yearConstruction = yearPicker.value.toString()
        }
    }


}
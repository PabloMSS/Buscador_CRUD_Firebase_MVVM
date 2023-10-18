package com.example.buscador_cruceros

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.*
import androidx.core.content.ContextCompat
import com.example.buscador_cruceros.Models.Crucero
import com.example.buscador_cruceros.Models.Naviera
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.w3c.dom.Text

class AddCrucero : AppCompatActivity() {
    val db = Firebase.firestore
    val storage = Firebase.storage
    val storageRef = storage.reference

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editorPreferences: SharedPreferences.Editor

    var listNavieras = mutableListOf<String>()
    var yearConstruction: String = ""

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
        createSharedPreferences()
        getAllNavieras()
        uploadImgStorage()
        getCompany()
        fragmentYear()
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
        spNavieras.setAdapter(adapterSpinner)

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
                Toast.makeText(spNavieras.context, getString(R.string.mensaje_Naviera), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun createInfoDescripcion(descripcion: String): String {
        var listDescripcion = descripcion.split(".")
        var infoDescripcion = listDescripcion[0]+"."
        return infoDescripcion
    }

    fun addCruise(idNaviera: String){
        btnAdd.setOnClickListener {
            var nameNaviera = idNaviera
            var getUriReferences = Uri.parse(sharedPreferences.getString("imgURI", null))
            var idDocument = db.collection("crucero").document().id
            var nameCrucero = etNameCruise.text.toString()
            var descripcion = etDescripcion.text.toString()
            var infoDescripcion = createInfoDescripcion(etDescripcion.text.toString())
            var pasajeros = etPasajeros.text.toString().toInt()
            var tripulantes = etTripulantes.text.toString().toInt()
            var tonelaje = etTonelaje.text.toString().toInt()

            if(idDocument.isNotEmpty() && nameCrucero.isNotEmpty() && descripcion.isNotEmpty() && infoDescripcion.isNotEmpty() && nameNaviera.isNotEmpty() && pasajeros != null && idDocument.isNotEmpty() && tripulantes != null && tonelaje != null && yearConstruction != ""){
                var crucero = Crucero(idDocument, nameCrucero, descripcion, infoDescripcion, nameNaviera, pasajeros, yearConstruction, idDocument, tripulantes, etTonelaje.text.toString().toInt())
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
            }else{
                Toast.makeText(this, "Debes de rellenar todos los campos", Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("NewApi")
    fun fragmentYear(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_year)

        yearPicker = dialog.findViewById(R.id.yearPicker)
        btnYearConfirm = dialog.findViewById(R.id.btnYearConfirm)
        yearPicker.textColor = ContextCompat.getColor(this, R.color.white)
        yearPicker.minValue = 1900
        yearPicker.maxValue = 2030
        yearPicker.value = 2023

        yearPicker.setOnValueChangedListener { _, _, newVal ->
            yearConstruction = newVal.toString()
        }

        imgCalendar.setOnClickListener {
            dialog.show()
        }

        btnYearConfirm.setOnClickListener {
            dialog.hide()
            tvYearConstruction.text = getString(R.string.text_año)+": ${yearConstruction}"
            tvYearConstruction.setTypeface(null, Typeface.NORMAL)
        }
    }


}
package com.example.buscador_cruceros

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.buscador_cruceros.Models.Crucero
import com.example.buscador_cruceros.Models.Naviera
import com.example.buscador_cruceros.ViewModel.EditViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EditCrucero : AppCompatActivity() {
    var storage = Firebase.storage

    var imgUpload = false
    var seletedImg = false

    lateinit var viewModel: EditViewModel
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editorPreferences: SharedPreferences.Editor

    lateinit var listNavieras: List<String>
    lateinit var crucero: Crucero

    lateinit var etEditNameCruise: TextView
    lateinit var spEditNavieras: AutoCompleteTextView
    lateinit var tvYearEditConstruction: TextView
    lateinit var etEditTonelaje: TextView
    lateinit var etEditPasajeros: TextView
    lateinit var etEditTripulantes: TextView
    lateinit var imgEditUpload: ImageView
    lateinit var imgEditCalendar: ImageView
    lateinit var etEditDescripcion: EditText
    lateinit var btnEditAdd: Button

    lateinit var yearPicker: NumberPicker
    lateinit var btnYearConfirm: Button

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_crucero)
        initComponents()
        initUI()
    }

    fun initComponents(){
        etEditNameCruise = findViewById(R.id.etEditNameCruise)
        spEditNavieras = findViewById(R.id.spEditNavieras)
        tvYearEditConstruction = findViewById(R.id.tvYearEditConstruction)
        etEditTonelaje = findViewById(R.id.etEditTonelaje)
        etEditPasajeros = findViewById(R.id.etEditPasajeros)
        etEditTripulantes = findViewById(R.id.etEditTripulantes)
        imgEditUpload = findViewById(R.id.imgEditUpload)
        imgEditCalendar = findViewById(R.id.imgEditCalendar)
        etEditDescripcion = findViewById(R.id.etEditDescripcion)
        btnEditAdd = findViewById(R.id.btnEditAdd)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun initUI(){
        crucero = intent.extras?.getSerializable("editCrucero") as Crucero
        viewModel = ViewModelProvider(this).get(EditViewModel::class.java)
        createSharedProferences()
        viewModel.getNavieras()
        viewModel.listNaviera.observe(this){ result ->
            listNavieras = result
            var adapterSpinner = ArrayAdapter(this, R.layout.list_item, listNavieras)
            spEditNavieras.setAdapter(adapterSpinner)
            spEditNavieras.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Este método se llama cuando no se selecciona ningún elemento en el Spinner
                    // Puedes manejarlo si es necesario
                    Toast.makeText(spEditNavieras.context, "Nada Seleccionado", Toast.LENGTH_LONG).show()
                }
            }
        }

        imgEditCalendar.setOnClickListener {
            chageYearConstruction()
        }
        btnEditAdd.setOnClickListener {
            changeData()
            deleteUpdateImg()
            viewModel.editCrucero(crucero)
            viewModel.imgUpload.observe(this){ result ->
                if(result == true){
                    var intent = Intent(this, BuscadorCrucero::class.java)
                    startActivity(intent)
                }
            }
        }
        obtenerExtras()
        changeData()
        setImg()
        selectedImg()
    }

    fun createSharedProferences(){
        sharedPreferences = getSharedPreferences("ImgPreferences", Context.MODE_PRIVATE)
        editorPreferences= sharedPreferences.edit()
    }

    fun obtenerExtras(){
        etEditNameCruise.text = crucero.name
        etEditTonelaje.text = crucero.tonelaje.toString()
        etEditPasajeros.text = crucero.capacity.toString()
        etEditTripulantes.text = crucero.tripulantes.toString()
        etEditDescripcion.setText(crucero.description.toString())
        tvYearEditConstruction.text = getString(R.string.year_construction)+": "+crucero.yearConstruction
        tvYearEditConstruction.setTypeface(null, Typeface.NORMAL)
    }

    fun setImg(){
        val referenciaImagen = storage.reference.child("cruceros/${crucero.image.toString()}.png")

        referenciaImagen.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(imgEditUpload.context).load(uri).into(imgEditUpload)
        }.addOnFailureListener {
            Log.i("IMG NOT WORKING", it.toString())
        }
    }

    fun deleteUpdateImg(){
        var cont = 0
        val referenciaImagen = storage.reference.child("cruceros")
        viewModel.imgUploadMensaje(true)
        if(seletedImg == true){
            viewModel.imgUploadMensaje(false)
            referenciaImagen.listAll()
                .addOnSuccessListener { resultList ->
                    for(item in resultList.items){
                        if(item.name.equals("${crucero.image.toString()}.png")){
                            var referenceImgOld = storage.reference.child("cruceros/${crucero.image.toString()}.png")
                            referenceImgOld.delete()
                                .addOnSuccessListener {
                                    addImg()
                                    cont++
                                }
                        }
                    }
                    if(cont == 0){
                        addImg()
                    }
                }
        }
    }

    fun addImg(){
        var imgUri = Uri.parse(sharedPreferences.getString("imgUriEditor", null))
        var referenceImg = storage.reference.child("cruceros/${crucero.image.toString()}.png")
        referenceImg.putFile(imgUri)
            .addOnSuccessListener {
                viewModel.imgUploadMensaje(true)
                Log.i("Result", "Okey Upload Image")
            }
            .addOnFailureListener{
                Log.i("Result", "Error Upload Image")
            }
    }

    fun selectedImg(){
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if(uri != null){
                imgEditUpload.setImageURI(uri)
                editorPreferences.putString("imgUriEditor", uri.toString())
                editorPreferences.apply()
                seletedImg = true
            }else{
                Toast.makeText(this, "Debes de Seleccionar una Imagen", Toast.LENGTH_LONG).show()
            }
        }
        imgEditUpload.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun chageYearConstruction(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_year)
        dialog.show()

        yearPicker = dialog.findViewById(R.id.yearPicker)
        btnYearConfirm = dialog.findViewById(R.id.btnYearConfirm)
        yearPicker.maxValue = 2050
        yearPicker.minValue = 1900
        yearPicker.value = 2023
        yearPicker.textColor = ContextCompat.getColor(this, R.color.white)

        btnYearConfirm.setOnClickListener {
            dialog.hide()
            tvYearEditConstruction.text = getString(R.string.year_construction)+": "+yearPicker.value.toString()
            crucero.yearConstruction = yearPicker.value.toString()
        }
    }

    fun createInfoDescripcion(descripcion: String): String {
        var listDescripcion = descripcion.split(".")
        var infoDescripcion = listDescripcion[0]+"."
        return infoDescripcion
    }

    fun changeData(){
        crucero.name = etEditNameCruise.text.toString()
        crucero.tonelaje = etEditTonelaje.text.toString().toInt()
        crucero.capacity = etEditPasajeros.text.toString().toInt()
        crucero.tripulantes = etEditTripulantes.text.toString().toInt()
        crucero.description = etEditDescripcion.text.toString()
        crucero.infoDescription = createInfoDescripcion(crucero.description.toString())
    }
}
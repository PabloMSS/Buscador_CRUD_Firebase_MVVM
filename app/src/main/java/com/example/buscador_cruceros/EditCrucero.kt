package com.example.buscador_cruceros

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.buscador_cruceros.Models.Crucero
import com.example.buscador_cruceros.Models.Naviera
import com.example.buscador_cruceros.ViewModel.EditViewModel

class EditCrucero : AppCompatActivity() {

    lateinit var viewModel: EditViewModel

    lateinit var listNavieras: List<String>
    lateinit var crucero: Crucero

    lateinit var etEditNameCruise: TextView
    lateinit var spEditNavieras: AutoCompleteTextView
    lateinit var tvYearEditConstruction: TextView
    lateinit var etEditTonelaje: TextView
    lateinit var etEditPasajeros: TextView
    lateinit var etEditTripulantes: TextView
    lateinit var imgEditUpload: ImageView
    lateinit var etEditDescripcion: EditText
    lateinit var btnEditAdd: Button

    lateinit var yearPicker: NumberPicker
    lateinit var btnYearConfirm: Button
    lateinit var imgCalendar: ImageView

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
        etEditDescripcion = findViewById(R.id.etEditDescripcion)
        btnEditAdd = findViewById(R.id.btnEditAdd)
    }

    fun initUI(){
        crucero = intent.extras?.getSerializable("editCrucero") as Crucero
        viewModel = ViewModelProvider(this).get(EditViewModel::class.java)
        viewModel.getNavieras()
        viewModel.listNaviera.observe(this){ result ->
            listNavieras = result
            var adpaterSpinner = ArrayAdapter(this, R.layout.list_item, listNavieras)
            spEditNavieras.setAdapter(adpaterSpinner)
        }
        obtenerExtras()
        //getNavieraSelected()
    }

    fun obtenerExtras(){
        etEditNameCruise.text = crucero.name
        etEditTonelaje.text = crucero.tonelaje.toString()
        etEditPasajeros.text = crucero.capacity.toString()
        etEditTripulantes.text = crucero.tripulantes.toString()
        etEditDescripcion.setText(crucero.description.toString())
    }

    fun getNavieraSelected(){
        viewModel.getNameNaviera(crucero.company.toString())
        viewModel.nameNaviera.observe(this){ name ->
            Toast.makeText(this, name, Toast.LENGTH_LONG).show()
            for ((index, naviera) in listNavieras.withIndex()) {
                Toast.makeText(this, naviera, Toast.LENGTH_LONG).show()
                if (naviera.equals(name)) {
                    spEditNavieras.setSelection(index)
                    break
                }
            }

        }
    }
}
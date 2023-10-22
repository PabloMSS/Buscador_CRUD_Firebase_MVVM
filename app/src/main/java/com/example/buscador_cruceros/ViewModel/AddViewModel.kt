package com.example.buscador_cruceros.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.buscador_cruceros.Models.Crucero
import com.example.buscador_cruceros.Models.Naviera
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AddViewModel: ViewModel() {
    val db = Firebase.firestore
    val storage = Firebase.storage
    val storageRef = storage.reference

    private val _idNaviera: MutableLiveData<String> = MutableLiveData()
    val idNaviera: LiveData<String>
        get() = _idNaviera

    fun getCompanyFirebase(elementoSeleccionado: String) {
        db.collection("navieras")
            .get()
            .addOnSuccessListener {
                for(item in it){
                    var naviera: Naviera = item.toObject(Naviera::class.java)
                    if(naviera.name.equals(elementoSeleccionado)){
                        _idNaviera.value = naviera.id
                    }
                }
            }
            .addOnFailureListener {
            }
    }

    fun addCrucero(crucero: Crucero){
        db.collection("crucero")
            .document(crucero.id.toString())
            .set(crucero)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }
}
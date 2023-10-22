package com.example.buscador_cruceros.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.buscador_cruceros.Models.Crucero
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BuscadorViewModel: ViewModel() {

    val db = Firebase.firestore

    private val _listCrucerosBD: MutableLiveData<MutableList<Crucero>> = MutableLiveData()
    val listCrucerosBD: LiveData<MutableList<Crucero>>
        get() = _listCrucerosBD

    fun getAll(){
        var list = mutableListOf<Crucero>()
        db.collection("crucero")
            .get()
            .addOnSuccessListener {
                for(item in it){
                    var crucero : Crucero = item.toObject(Crucero::class.java)
                    list.add(crucero)
                    Log.i("prueba", crucero.id.toString())
                }
                _listCrucerosBD.value = list
                Log.i("prueba2", _listCrucerosBD.value!!.size.toString())
            }.addOnFailureListener {

            }
    }

    fun deleteCrucero(id: String){
        db.collection("crucero")
            .document(id)
            .delete()
            .addOnSuccessListener {

            }.addOnFailureListener {

            }
    }
}
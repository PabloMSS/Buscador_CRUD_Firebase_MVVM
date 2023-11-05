package com.example.buscador_cruceros.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DetalleViewModel: ViewModel() {

    private val db = Firebase.firestore

    private val _naviera :MutableLiveData<String> =  MutableLiveData()
    val naviera: LiveData<String>
        get() = _naviera

    fun getNaviera(id: String){
        db.collection("navieras")
            .document(id)
            .get()
            .addOnSuccessListener {
                _naviera.value = it.get("name").toString()
            }
            .addOnFailureListener {
                Log.i("comapny", "Error")
            }
    }
}
package com.example.buscador_cruceros.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.buscador_cruceros.Models.Crucero
import com.example.buscador_cruceros.Models.Naviera
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class EditViewModel: ViewModel() {

    val db = Firebase.firestore

    private val _listNavieras: MutableLiveData<List<String>> = MutableLiveData()

    val listNaviera: LiveData<List<String>>
        get() = _listNavieras

    private val _nameNaviera: MutableLiveData<String> = MutableLiveData()

    val nameNaviera: LiveData<String>
        get() = _nameNaviera

    fun getNavieras(){
        var list = mutableListOf<String>()
        db.collection("navieras")
            .get()
            .addOnSuccessListener { result ->
                for (item in result){
                    var naviera = item.toObject(Naviera::class.java)
                    list.add(naviera.name)
                }
                _listNavieras.value = list
            }
            .addOnFailureListener {

            }
    }

    fun getNameNaviera(id:String){
        db.collection("navieras")
            .document(id)
            .get()
            .addOnSuccessListener { result ->
                var naviera = result.toObject(Naviera::class.java)
                _nameNaviera.value = naviera?.name
            }
            .addOnFailureListener {

            }
    }
}
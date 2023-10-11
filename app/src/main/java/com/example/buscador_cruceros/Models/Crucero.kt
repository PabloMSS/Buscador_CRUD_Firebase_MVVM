package com.example.buscador_cruceros.Models

import java.io.Serializable

data class Crucero(
    var id: String? = "",
    var name: String? = "",
    var description: String? = "",
    var infoDescription: String? = "",
    var company: String? = "",
    var capacity: Int? = 0,
    var yearConstruction: String? = "",
    var image: String? = "",
    var tripulantes: Int? = 0,
    var tonelaje: Int? = 0
): Serializable
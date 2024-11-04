package com.example.practico4.models

import java.io.Serializable

data class Telefono(
    val id: Int,
    var number: String,
    val persona_id: Int,
    val label: String
)

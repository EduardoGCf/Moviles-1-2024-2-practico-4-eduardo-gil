package com.example.practico4.models

import java.io.Serializable

data class Contacto(
    val id: Int,
    val name: String,
    val last_name: String,
    val company: String?,
    val address: String?,
    val city: String?,
    val state: String?,
    val profile_picture: String?,
    val phones: List<Telefono> = emptyList(),
    val emails: List<Email> = emptyList()
)
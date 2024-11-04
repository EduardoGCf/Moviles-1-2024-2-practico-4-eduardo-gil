package com.example.practico4.models

import java.io.Serializable

data class Email(
    val id: Int,
    val email: String,
    val persona_id: Int,
    val label: String
)
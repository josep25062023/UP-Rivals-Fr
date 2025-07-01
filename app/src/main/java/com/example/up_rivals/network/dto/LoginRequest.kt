package com.example.up_rivals.network.dto // Asegúrate que el nombre del paquete coincida con el tuyo

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)
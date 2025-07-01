// En: network/dto/LoginResponse.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

// Este es el único DTO que necesitamos para la respuesta de login
data class LoginResponse(
    @SerializedName("accessToken")
    val accessToken: String
)

// La data class 'User' ya no es necesaria EN ESTE ARCHIVO, la eliminamos.
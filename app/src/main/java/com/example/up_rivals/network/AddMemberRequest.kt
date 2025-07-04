// En: network/dto/AddMemberRequest.kt
package com.example.up_rivals.network.dto

import com.google.gson.annotations.SerializedName

// Versión corregida: ahora envía el ID del jugador
data class AddMemberRequest(
    // Asumimos que el nombre del campo en el JSON es "userId"
    // si tu amigo lo llamó diferente (ej. "id"), solo hay que cambiarlo aquí.
    @SerializedName("userId")
    val userId: String
)